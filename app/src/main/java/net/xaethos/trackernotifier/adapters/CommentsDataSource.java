package net.xaethos.trackernotifier.adapters;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.models.Comment;

import java.util.ArrayList;
import java.util.Collection;

public class CommentsDataSource extends ResourceDataSource<Comment> {

    private ArrayList<Comment> mItems = new ArrayList<>();

    public static ResourceAdapter<Comment, CommentsDataSource> createAdapter() {
        CommentsDataSource dataSource = new CommentsDataSource();
        ResourceAdapter<Comment, CommentsDataSource> adapter = new ResourceAdapter<>(dataSource);
        adapter.setHasStableIds(true);
        dataSource.setObserver(adapter);
        return adapter;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_comment;
    }

    @Override
    public Comment getResource(int position) {
        return mItems.get(position);
    }

    public void setComments(Collection<Comment> comments) {
        mItems.clear();
        if (comments != null) mItems.addAll(comments);
        notifyDataSetChanged();
    }

}
