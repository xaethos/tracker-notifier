package net.xaethos.trackernotifier.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class NotificationsAdapter extends RecyclerView.Adapter<ResourceViewHolder>
        implements DataSource.Observer {

    private LayoutInflater mLayoutInflater;
    private final NotificationsDataSource mDataSource;

    public static NotificationsAdapter create() {
        NotificationsDataSource dataSource = new NotificationsDataSource();
        NotificationsAdapter adapter = new NotificationsAdapter(dataSource);
        adapter.setHasStableIds(true);
        dataSource.setObserver(adapter);
        return adapter;
    }

    NotificationsAdapter(NotificationsDataSource dataSource) {
        super();
        mDataSource = dataSource;
    }

    public NotificationsDataSource getDataSource() {
        return mDataSource;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mLayoutInflater = null;
    }

    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ResourceViewHolder.create(mLayoutInflater, viewType, parent);
    }

    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        holder.bind(mDataSource.getResource(position));
    }

    @Override
    public long getItemId(int position) {
        return mDataSource.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mDataSource.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSource.getItemViewType(position);
    }

}
