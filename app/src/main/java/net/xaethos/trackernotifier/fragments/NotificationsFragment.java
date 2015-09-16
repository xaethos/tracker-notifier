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
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import java.util.Collections;
import java.util.List;

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

        public final TextView textProjectName;
        public final TextView textStoryName;
        public final TextView textNotifMessage;
        public final TextView textNotifContext;

        public NotificationItemViewHolder(View itemView) {
            super(itemView);
            textProjectName = (TextView) itemView.findViewById(R.id.text_project_name);
            textStoryName = (TextView) itemView.findViewById(R.id.text_story_name);
            textNotifMessage = (TextView) itemView.findViewById(R.id.text_notif_message);
            textNotifContext = (TextView) itemView.findViewById(R.id.text_notif_context);
        }
    }

    private static class NotificationsAdapter
            extends RecyclerView.Adapter<NotificationItemViewHolder> {

        private final LayoutInflater mLayoutInflater;
        private List<Notification> mNotifications;

        private NotificationsAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mNotifications = Collections.emptyList();
            setHasStableIds(true);
        }

        @Override
        public NotificationItemViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            final View itemView =
                    mLayoutInflater.inflate(R.layout.item_notification, parent, false);
            return new NotificationItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(
                NotificationItemViewHolder holder, int position) {
            Notification notification = mNotifications.get(position);
            holder.textProjectName.setText(notification.project.name);
            holder.textStoryName.setText(notification.story.name);
            holder.textNotifMessage.setText(notification.message);
            holder.textNotifContext.setText(notification.context);
        }

        @Override
        public long getItemId(int position) {
            return mNotifications.get(position).id;
        }

        @Override
        public int getItemCount() {
            return mNotifications.size();
        }

        public void setNotifications(List<Notification> notifications) {
            if (notifications == null) notifications = Collections.emptyList();
            mNotifications = notifications;
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
