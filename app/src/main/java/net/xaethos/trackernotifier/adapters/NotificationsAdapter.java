package net.xaethos.trackernotifier.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Story;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NotificationsAdapter
        extends RecyclerView.Adapter<NotificationItemViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private ArrayList<Object> mData;

    public NotificationsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public NotificationItemViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        final View itemView = mLayoutInflater.inflate(viewType, parent, false);
        return new NotificationItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            NotificationItemViewHolder holder, int position) {
        Object item = mData.get(position);

        if (item instanceof Notification) {
            holder.title.setText(((Notification) item).message);
            holder.summary.setText(((Notification) item).context);
            holder.initials.setText(((Notification) item).performer.initials);
        } else if (item instanceof Story) {
            holder.title.setText(((Story) item).name);
        } else if (item instanceof Project) {
            holder.title.setText(((Project) item).name);
        }
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        Object item = mData.get(position);
        if (item instanceof Notification) return ((Notification) item).id;
        if (item instanceof Story) return ((Story) item).id;
        if (item instanceof Project) return ((Project) item).id;
        return 0;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mData.get(position);
        if (item instanceof Notification) return R.layout.item_notification;
        if (item instanceof Story) return R.layout.item_story;
        if (item instanceof Project) return R.layout.item_project;
        return super.getItemViewType(position);
    }

    public void setNotifications(List<Notification> notifications) {
        Map<Project, Map<Story, List<Notification>>> projectMap = new LinkedHashMap<>();

        int count = 0;
        for (Notification notification : notifications) {
            if (notification.read_at != null) continue; // Skip notifications marked as read
            count++; // one notification item

            Map<Story, List<Notification>> storyMap = projectMap.get(notification.project);
            if (storyMap == null) {
                count++; // one project header
                storyMap = new LinkedHashMap<>();
                projectMap.put(notification.project, storyMap);
            }

            List<Notification> storyNotifications = storyMap.get(notification.story);
            if (storyNotifications == null) {
                count++; // one story header
                storyNotifications = new ArrayList<>();
                storyMap.put(notification.story, storyNotifications);
            }

            storyNotifications.add(notification);
        }

        ArrayList<Object> data = new ArrayList<>(count);
        for (Map.Entry<Project, Map<Story, List<Notification>>> projectEntry : projectMap.entrySet()) {
            data.add(projectEntry.getKey());
            for (Map.Entry<Story, List<Notification>> storyEntry : projectEntry.getValue()
                    .entrySet()) {
                data.add(storyEntry.getKey());
                data.addAll(storyEntry.getValue());
            }
        }

        mData = data;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

}
