package net.xaethos.trackernotifier.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xaethos.trackernotifier.MainActivity;
import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Project;
import net.xaethos.trackernotifier.models.Story;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends Fragment {

    NotificationsAdapter mAdapter;
    MainActivity mMainActivity;
    private Subscription mSubscription;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        mMainActivity = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getContext();
        mAdapter = new NotificationsAdapter(context);

        RecyclerView recyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerDecorator(context));
        recyclerView.setAdapter(mAdapter);

        return recyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubscription = subscribeAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onPause();
    }

    private Subscription subscribeAdapter(final NotificationsAdapter adapter) {
        return new TrackerClient().getNotificationsApi()
                .notifications(mMainActivity.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<List<Notification>>(getContext()) {
                    @Override
                    public void onNext(List<Notification> notifications) {
                        adapter.setNotifications(notifications);
                    }
                });
    }

    private static class NotificationItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView summary;

        public NotificationItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            summary = (TextView) itemView.findViewById(R.id.summary);
        }
    }

    private static class NotificationsAdapter
            extends RecyclerView.Adapter<NotificationItemViewHolder> {

        private final LayoutInflater mLayoutInflater;
        private ArrayList<Object> mData;

        private NotificationsAdapter(Context context) {
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
            } else if (item instanceof Story) {
                holder.title.setText(((Story) item).name);
            } else if (item instanceof Project) {
                holder.title.setText(((Project) item).name);
            }
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
            for (Map.Entry<Project, Map<Story, List<Notification>>> projectEntry : projectMap
                    .entrySet()) {
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
    }

    private static class DividerDecorator extends RecyclerView.ItemDecoration {

        private final Drawable mDivider;
        private final int mDividerHeight;

        public DividerDecorator(Context context) {
            TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            mDivider = a.getDrawable(0);
            mDividerHeight = mDivider == null ? 0 : mDivider.getIntrinsicHeight();
            a.recycle();
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, mDividerHeight);
        }

        @Override
        public void onDraw(
                Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mDividerHeight == 0) return;

            final RecyclerView.LayoutManager manager = parent.getLayoutManager();
            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                final int left = manager.getDecoratedLeft(child);
                final int right = manager.getDecoratedRight(child);
                final int bottom = manager.getDecoratedBottom(child);
                final int top = bottom - mDividerHeight;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
