package net.xaethos.trackernotifier.adapters;

import android.support.annotation.NonNull;

import net.xaethos.trackernotifier.R;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.models.Resource;
import net.xaethos.trackernotifier.models.Story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsDataSource implements DataSource {

    private Observer mObserver;
    private ArrayList<Item> mItems = new ArrayList<>(12);

    private static class Item {
        public final Resource resource;
        //public final int level;
        public int size;
        public int parentOffset;

        private Item(Resource resource) {
            this.resource = resource;
        }
    }

    @Override
    public void setObserver(Observer observer) {
        mObserver = observer;
    }

    @Override
    public long getItemId(int position) {
        return getResource(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Resource item = getResource(position);
        if (item instanceof Notification) return R.layout.item_notification;
        if (item instanceof Story) return R.layout.item_story;
        return R.layout.item_project;
    }

    public Resource getResource(int position) {
        return mItems.get(position).resource;
    }

    public void addNotification(Notification notification) {
        addResourcePath(notification.project, notification.story, notification);
    }

    /**
     * Adds a series of nested resources
     *
     * @param resources the resources to add, outermost first
     */
    private void addResourcePath(@NonNull Resource... resources) {
        int insertPosition = -1;
        int insertCount = 0;

        int parentPosition = -1;
        int outOfBounds = mItems.size();

        for (Resource resource : resources) {
            Item item;
            int resourcePos = findResourcePosition(resource, parentPosition + 1, outOfBounds);
            if (resourcePos < 0) {
                resourcePos = outOfBounds;
                item = addResource(resource, resourcePos, parentPosition);

                if (insertPosition < 0) insertPosition = resourcePos;
                ++insertCount;
            } else {
                item = mItems.get(resourcePos);
            }
            parentPosition = resourcePos;
            outOfBounds = resourcePos + item.size;
        }

        if (insertCount > 0) notifyItemRangeInserted(insertPosition, insertCount);
    }

    /**
     * Searches for resource among the siblings in the [startPosition, outOfBounds) range.
     *
     * @return The index of the containing Item, or -1 if not found
     */
    private int findResourcePosition(Resource resource, int startPosition, int outOfBounds) {
        int currentPos = startPosition;
        while (currentPos < outOfBounds) {
            Item item = mItems.get(currentPos);
            if (item.resource.equals(resource)) return currentPos;
            currentPos += item.size;
        }
        return -1;
    }

    private Item addResource(Resource resource, int position, int parentPosition) {
        Item item = new Item(resource);
        item.size = 1;
        item.parentOffset = parentPosition - position;
        mItems.add(position, item);

        // Adjust parents' sizes
        while (parentPosition >= 0) {
            Item parent = mItems.get(parentPosition);
            ++parent.size;
            parentPosition += parent.parentOffset;
        }

        return item;
    }

    private void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (mObserver != null) mObserver.notifyItemRangeInserted(positionStart, itemCount);
    }

    public List<Notification> removeResource(int position) {
        return Collections.emptyList();
    }

}
