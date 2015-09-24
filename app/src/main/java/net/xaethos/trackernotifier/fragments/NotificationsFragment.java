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

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends Fragment {

    MainActivity mMainActivity;
    NotificationsAdapter mAdapter;
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
        mAdapter = NotificationsAdapter.create();
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
                Observable.from(mAdapter.getDataSource().removeItem(position))
                        .flatMap(notif -> mApiClient.notifications().markRead(notif.id))
                        .subscribe(notif -> Log.d("XAE", "notification read" + notif.id),
                                error -> Log.d("XAE", "markRead error", error));

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
        mSubscription = subscribeDataSource(mAdapter.getDataSource());
    }

    @Override
    public void onPause() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onPause();
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

}
