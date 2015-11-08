package net.xaethos.trackernotifier.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.models.*
import net.xaethos.trackernotifier.utils.empty

abstract class ResourceViewHolder<T : Resource>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}

class NotificationViewHolder(itemView: View) : ResourceViewHolder<Notification>(itemView) {
    val title = itemView.findViewById(R.id.title) as TextView
    val summary = itemView.findViewById(R.id.summary) as TextView

    override fun bind(item: Notification) {
        title.text = item.message

        if (item.context.empty) {
            summary.visibility = View.GONE
        } else {
            summary.text = item.context
            summary.visibility = View.VISIBLE
        }
    }
}

class StoryViewHolder(itemView: View) : ResourceViewHolder<Story>(itemView) {
    val title = itemView.findViewById(R.id.title) as TextView
    val icon = itemView.findViewById(R.id.icon) as ImageView

    override fun bind(item: Story) {
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

class ProjectViewHolder(itemView: View) : ResourceViewHolder<Project>(itemView) {
    val title = itemView.findViewById(R.id.title) as TextView

    override fun bind(item: Project) {
        title.text = item.name
    }
}

class CommentViewHolder(itemView: View) : ResourceViewHolder<Comment>(itemView) {
    val initials = itemView.findViewById(R.id.person_initials) as TextView
    val name = itemView.findViewById(R.id.person_name) as TextView
    val username = itemView.findViewById(R.id.person_username) as TextView
    val text = itemView.findViewById(R.id.text) as TextView

    override fun bind(item: Comment) {
        text.text = item.text
        val person = item.person
        if (person != null) {
            initials.text = person.initials
            name.text = person.name
            username.text = "@${person.username}"
        }
    }
}
