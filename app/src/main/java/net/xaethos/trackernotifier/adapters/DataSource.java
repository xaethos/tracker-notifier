package net.xaethos.trackernotifier.adapters;

public interface DataSource {
    long getItemId(int position);

    int getItemCount();

    int getItemViewType(int position);

    void setObserver(Observer observer);

    interface Observer {
        void notifyItemRangeInserted(int positionStart, int itemCount);
    }
}
