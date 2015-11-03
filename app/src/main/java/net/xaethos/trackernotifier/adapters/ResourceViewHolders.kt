package net.xaethos.trackernotifier.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.models.*
import net.xaethos.trackernotifier.utils.empty

abstract class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById(R.id.title) as TextView

    abstract fun bind(item: Resource)
}

class NotificationViewHolder(itemView: View) : ResourceViewHolder(itemView) {
    val summary = itemView.findViewById(R.id.summary) as TextView

    override fun bind(item: Resource) {
        if (item !is Notification) throw IllegalArgumentException("must bind to notification")

        title.text = item.message

        if (item.context.empty) {
            summary.visibility = View.GONE
        } else {
            summary.text = item.context
            summary.visibility = View.VISIBLE
        }
    }
}

class StoryViewHolder(itemView: View) : ResourceViewHolder(itemView) {
    val icon = itemView.findViewById(R.id.icon) as ImageView

    override fun bind(item: Resource) {
        if (item !is Story) throw IllegalArgumentException("must bind to story")
        title.text = item.name
        when (item.story_type) {
            Story.TYPE_FEATURE -> icon.setImageResource(R.drawable.ic_star_black_18dp)
            Story.TYPE_CHORE -> icon.setImageResource(R.drawable.ic_settings_black_18dp)
            Story.TYPE_BUG -> icon.setImageResource(R.drawable.ic_bug_report_black_18dp)
            Story.TYPE_RELEASE -> icon.setImageResource(R.drawable.ic_flag_black_18dp)
            else -> icon.setImageBitmap(null)
        }
    }
}

class ProjectViewHolder(itemView: View) : ResourceViewHolder(itemView) {
    override fun bind(item: Resource) {
        if (item !is Project) throw IllegalArgumentException("must bind to project")
        title.text = item.name
    }
}

class CommentViewHolder(itemView: View) : ResourceViewHolder(itemView) {
    override fun bind(item: Resource) {
        if (item !is Comment) throw IllegalArgumentException("must bind to comment")
        title.text = item.text
    }
}
