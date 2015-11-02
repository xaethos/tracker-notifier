package net.xaethos.trackernotifier.adapters;

import net.xaethos.trackernotifier.models.Resource;

public abstract class ResourceDataSource<R extends Resource> implements DataSource {

    private Observer mObserver;

    public abstract R getResource(int position);

    @Override
    public long getItemId(int position) {
        return getResource(position).hashCode();
    }

    @Override
    public void setObserver(Observer observer) {
        mObserver = observer;
    }

    protected void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (mObserver != null) mObserver.notifyItemRangeInserted(positionStart, itemCount);
    }

    protected void notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (mObserver != null) mObserver.notifyItemRangeRemoved(positionStart, itemCount);
    }

    protected void notifyDataSetChanged() {
        if (mObserver != null) mObserver.notifyDataSetChanged();
    }

}
