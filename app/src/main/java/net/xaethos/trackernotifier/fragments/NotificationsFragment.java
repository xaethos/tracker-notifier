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

import net.xaethos.trackernotifier.MainActivity;
import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.adapters.DividerDecorator;
import net.xaethos.trackernotifier.adapters.NotificationsAdapter;
import net.xaethos.trackernotifier.adapters.NotificationsDataSource;
import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends Fragment {

    MainActivity mMainActivity;
    NotificationsDataSource mDataSource;
    TrackerClient mApiClient;

    private Subscription mSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        mMainActivity = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiClient = TrackerClient.getInstance();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsAdapter adapter = NotificationsAdapter.create();
        mDataSource = adapter.getDataSource();

        Context context = container.getContext();
        RecyclerView recyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerDecorator(context));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(
                    RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Object item = mDataSource.getResource(position);
                if (item instanceof Notification) {
                    List<Notification> removedNotifications = mDataSource.removeResource(position);
                    for (Notification notification : removedNotifications) {
                        mApiClient.notifications()
                                .markRead(notification.id)
                                .subscribe(notif -> Log.d("XAE", "notification read" + notif.id),
                                        error -> Log.d("XAE", "markRead error", error));
                    }

                }
            }
        }).attachToRecyclerView(recyclerView);

        return recyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApiClient.setToken(mMainActivity.getToken());
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubscription = subscribeAdapter(mDataSource);
    }

    @Override
    public void onPause() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onPause();
    }

    private Subscription subscribeAdapter(final NotificationsDataSource adapter) {
        return mApiClient.notifications()
                .get()
                .flatMap(Observable::from)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Notification>(getContext()) {
                    @Override
                    public void onNext(Notification notification) {
                        adapter.addNotification(notification);
                    }
                });
    }

}
