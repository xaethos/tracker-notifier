package net.xaethos.trackernotifier.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.adapters.CommentsDataSource;
import net.xaethos.trackernotifier.adapters.ResourceAdapter;
import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Comment;
import net.xaethos.trackernotifier.models.Story;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class StoryCommentsFragment extends BaseResourceFragment<Story> {

    TrackerClient mApiClient;
    View mEmptyView;
    ResourceAdapter<Comment, CommentsDataSource> mAdapter;

    long mProjectId;
    Subscription mContentSubscription;

    public static StoryCommentsFragment newInstance(long projectId) {
        Bundle args = new Bundle(1);
        args.putLong("projectId", projectId);

        StoryCommentsFragment fragment = new StoryCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjectId = getArguments().getLong("projectId");
        mApiClient = TrackerClient.getInstance();
        mAdapter = CommentsDataSource.createAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(android.R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        mContentSubscription =
                observeResource().flatMap((story) -> mApiClient.comments.get(mProjectId, story.id))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((comments -> {
                            mAdapter.getDataSource().setComments(comments);
                        }));

        return root;
    }

    @Override
    public void onDestroyView() {
        mContentSubscription.unsubscribe();
        super.onDestroyView();
    }

}
