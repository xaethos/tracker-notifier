package net.xaethos.trackernotifier.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.adapters.NotificationsAdapter;
import net.xaethos.trackernotifier.adapters.NotificationsDataSource;
import net.xaethos.trackernotifier.adapters.NotificationsDividerDecorator;
import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.MultipleAssignmentSubscription;

public class NotificationsFragment extends Fragment {

    TrackerClient mApiClient;
    SwipeRefreshLayout mRefreshView;
    NotificationsAdapter mAdapter;

    private MultipleAssignmentSubscription mDataSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mApiClient = TrackerClient.getInstance(getContext());
        mAdapter = NotificationsAdapter.create();

        mDataSubscription = new MultipleAssignmentSubscription();
        refreshData();
    }

    @Override
    public void onDestroy() {
        mDataSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Context context = getContext();

        RecyclerView recyclerView = (RecyclerView) root.findViewById(android.R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new NotificationsDividerDecorator(context));
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new SwipeCallback()).attachToRecyclerView(recyclerView);

        mRefreshView = (SwipeRefreshLayout) root.findViewById(R.id.refresh);
        mRefreshView.setOnRefreshListener(this::refreshData);

        return root;
    }

    @Override
    public void onDestroyView() {
        mRefreshView = null;
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notification, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData() {
        mDataSubscription.set(subscribeDataSource(mAdapter.getDataSource()));
    }

    private Subscription subscribeDataSource(final NotificationsDataSource dataSource) {
        if (mRefreshView != null) mRefreshView.setRefreshing(true);
        return mApiClient.notifications()
                .get()
                .flatMap(Observable::from)
                .filter(notification -> notification.read_at == null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (mRefreshView != null) mRefreshView.setRefreshing(false);
                })
                .subscribe(new ErrorToastSubscriber<Notification>(getContext()) {
                    @Override
                    public void onNext(Notification notification) {
                        dataSource.addNotification(notification);
                    }
                });
    }

    void markAsRead(List<Notification> notifications) {
        View container = getView();
        if (container == null) return;
        Context context = container.getContext();

        int count = notifications.size();
        String message = context.getResources().getQuantityString(
                R.plurals.toast_notifications_read, count, count);

        final NotificationsDataSource dataSource = mAdapter.getDataSource();
        final Observable<Notification> readItems = Observable.from(notifications);

        Snackbar.make(container, message, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == DISMISS_EVENT_ACTION) return;

                        // User didn't undo the swipe, so actually mark notifications read
                        readItems.forEach(notification -> {
                            mApiClient.notifications().markRead(notification.id)
                                    .subscribe(n -> Log.i("XAE", "notification read: " + n.id),
                                            error -> {
                                                Log.d("XAE", "markRead error", error);
                                                dataSource.addNotification(notification);
                                            });
                        });
                    }
                })
                .setAction(R.string.action_undo, v -> {
                    readItems.subscribe(dataSource::addNotification);
                })
                .show();
    }

    private class SwipeCallback extends ItemTouchHelper.SimpleCallback {

        public SwipeCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            List<Notification> removed = mAdapter.getDataSource().removeItem(position);
            markAsRead(removed);
        }
    }

}
