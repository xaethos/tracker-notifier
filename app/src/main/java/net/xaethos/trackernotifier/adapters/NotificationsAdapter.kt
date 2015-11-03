package net.xaethos.trackernotifier.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.StoryActivity

import net.xaethos.trackernotifier.models.Notification
import net.xaethos.trackernotifier.models.Project
import net.xaethos.trackernotifier.models.Resource
import net.xaethos.trackernotifier.models.Story
import rx.Observable
import java.util.*

class NotificationsAdapter : RecyclerView.Adapter<ResourceViewHolder<out Resource>>(), View.OnClickListener {

    private class Item(val resource: Resource, var parentOffset: Int, var size: Int = 1)

    private val items = ArrayList<Item>(12)

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder<out Resource> {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        val viewHolder = when (viewType) {
            R.layout.item_notification -> NotificationViewHolder(itemView)
            R.layout.item_story -> StoryViewHolder(itemView)
            R.layout.item_project -> ProjectViewHolder(itemView)
            else -> throw IllegalArgumentException("unknown view type")
        }

        if (viewType in intArrayOf(R.layout.item_notification, R.layout.item_story)) {
            itemView.setOnClickListener(this)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ResourceViewHolder<out Resource>, position: Int) {
        val resource = get(position)
        when (holder) {
            is NotificationViewHolder -> holder.bind(resource as Notification)
            is StoryViewHolder -> holder.bind(resource as Story)
            is ProjectViewHolder -> holder.bind(resource as Project)
            else -> throw IllegalStateException("invalid ViewHolder for resource")
        }
    }

    operator fun get(i: Int) = items[i].resource

    override fun getItemId(position: Int) = get(position).hashCode().toLong()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (get(position)) {
        is Notification -> R.layout.item_notification
        is Story -> R.layout.item_story
        else -> R.layout.item_project
    }

    override fun onClick(v: View) {
        val position = recyclerView?.getChildAdapterPosition(v) ?: RecyclerView.NO_POSITION
        if (position == RecyclerView.NO_POSITION) return

        val projectId: Long
        val story: Story
        val resource = get(position)

        when (resource) {
            is Notification -> {
                story = resource.story
                projectId = resource.project.id
            }
            is Story -> {
                story = resource
                val projectPosition = position + items[position].parentOffset
                projectId = items[projectPosition].resource.id
            }
            else -> return
        }
        val context = v.context
        context.startActivity(StoryActivity.forStory(context, projectId, story))
    }

    fun addNotification(notification: Notification) =
            addResourcePath(notification.project, notification.story, notification)

    fun removeItem(position: Int): List<Notification> {
        val item = items[position]
        var removeStart = position
        var removeCount = item.size

        // Adjust parents' sizes and expand removal range as necessary
        var parentPosition = position + item.parentOffset
        while (parentPosition >= 0) {
            val parent = items[parentPosition]
            if (parent.size == removeCount + 1) {
                // Last child in parent; parent goes as well
                removeStart = parentPosition
                removeCount = parent.size
            } else {
                parent.size -= removeCount
            }
            parentPosition += parent.parentOffset
        }

        // Get the sublist to remove, and extract the notifications
        val removeSubList = items.subList(removeStart, removeStart + removeCount)
        val removedNotifications = ArrayList<Notification>(removeCount)
        Observable.from(removeSubList)
                .map({ it.resource })
                .forEach { if (it is Notification) removedNotifications.add(it) }

        // Do the removal and finish up
        removeSubList.clear()
        adjustOffsets(removeStart, -removeCount)
        notifyItemRangeRemoved(removeStart, removeCount)
        return removedNotifications
    }

    /**
     * Adds a series of nested resources

     * @param resources the resources to add, outermost first
     */
    private fun addResourcePath(vararg resources: Resource) {
        var insertPosition = -1
        var insertCount = 0

        var parentPosition = -1
        var outOfBounds = items.size

        for (resource in resources) {
            val item: Item
            var resourcePos = findResourcePosition(resource, parentPosition + 1, outOfBounds)
            if (resourcePos < 0) {
                resourcePos = outOfBounds
                item = addResource(resource, resourcePos, parentPosition)

                if (insertPosition < 0) insertPosition = resourcePos
                ++insertCount
            } else {
                item = items[resourcePos]
            }
            parentPosition = resourcePos
            outOfBounds = resourcePos + item.size
        }

        if (insertCount > 0) {
            adjustOffsets(insertPosition + insertCount, insertCount)
            notifyItemRangeInserted(insertPosition, insertCount)
        }
    }

    /**
     * Searches for resource among the siblings in the [startPosition, outOfBounds) range.

     * @return The index of the containing Item, or -1 if not found
     */
    private fun findResourcePosition(resource: Resource, startPosition: Int, outOfBounds: Int): Int {
        var currentPos = startPosition
        while (currentPos < outOfBounds) {
            val item = items[currentPos]
            if (item.resource == resource) return currentPos
            currentPos += item.size
        }
        return -1
    }

    private fun addResource(resource: Resource, position: Int, parentPosition: Int): Item {
        val item = Item(resource, parentOffset = parentPosition - position)
        items.add(position, item)

        tailrec fun adjustParentSize(parentPosition: Int) {
            if (parentPosition >= 0) {
                val parent = items[parentPosition]
                parent.size += 1
                adjustParentSize(parentPosition + parent.parentOffset)
            }
        }
        adjustParentSize(parentPosition)

        return item
    }

    /**
     * When inserting or removing items, we need to adjust the parent offset of "sibling" items that
     * come after.

     * @param start first position to adjust
     * @param delta the size change in the list, positive for inserts, negative for removals
     */
    tailrec private fun adjustOffsets(start: Int, delta: Int) {
        if (start < items.size) {
            val item = items[start]
            item.parentOffset -= delta
            adjustOffsets(start + item.size, delta)
        }
    }
}
