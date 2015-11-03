package net.xaethos.trackernotifier.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.xaethos.trackernotifier.models.Resource;

public class ResourceAdapter<R extends Resource, DS extends ResourceDataSource<R>>
        extends RecyclerView.Adapter<CommentViewHolder> implements DataSource.Observer {

    private LayoutInflater mLayoutInflater;
    private final DS mDataSource;

    ResourceAdapter(DS dataSource) {
        super();
        mDataSource = dataSource;
    }

    public DS getDataSource() {
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
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(mLayoutInflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
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
