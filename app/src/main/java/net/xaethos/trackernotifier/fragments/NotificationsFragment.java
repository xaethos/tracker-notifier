package net.xaethos.trackernotifier.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.adapters.DividerDecorator;
import net.xaethos.trackernotifier.adapters.NotificationsAdapter;
import net.xaethos.trackernotifier.adapters.NotificationsDataSource;
import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.MultipleAssignmentSubscription;

public class NotificationsFragment extends Fragment {

    NotificationsAdapter mAdapter;
    TrackerClient mApiClient;

    private MultipleAssignmentSubscription mDataSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiClient = TrackerClient.getInstance(getContext());
        mAdapter = NotificationsAdapter.create();

        mDataSubscription = new MultipleAssignmentSubscription();
        mDataSubscription.set(subscribeDataSource(mAdapter.getDataSource()));
    }

    @Override
    public void onDestroy() {
        mDataSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = getContext();
        RecyclerView recyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerDecorator(context));
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new SwipeCallback()).attachToRecyclerView(recyclerView);

        return recyclerView;
    }

    private Subscription subscribeDataSource(final NotificationsDataSource dataSource) {
        return mApiClient.notifications()
                .get()
                .flatMap(Observable::from)
                .filter(notification -> notification.read_at == null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Notification>(getContext()) {
                    @Override
                    public void onNext(Notification notification) {
                        dataSource.addNotification(notification);
                    }
                });
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
            Observable.from(mAdapter.getDataSource().removeItem(position))
                    .flatMap(notif -> mApiClient.notifications().markRead(notif.id))
                    .subscribe(notif -> Log.d("XAE", "notification read: " + notif.id),
                            error -> Log.d("XAE", "markRead error", error));
        }
    }

}
