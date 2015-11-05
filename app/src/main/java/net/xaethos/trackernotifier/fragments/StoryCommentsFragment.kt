package net.xaethos.trackernotifier.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.adapters.CommentViewHolder
import net.xaethos.trackernotifier.adapters.ResourceAdapter
import net.xaethos.trackernotifier.api.TrackerClient
import net.xaethos.trackernotifier.models.Story
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class StoryCommentsFragment : BaseResourceFragment<Story>() {
    private val apiClient = TrackerClient.instance

    private val adapter = ResourceAdapter(R.layout.item_comment) { inflater, viewType, parent ->
        CommentViewHolder(inflater.inflate(viewType, parent, false))
    }

    private var projectId: Long = 0
    private var contentSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments.getLong("projectId")
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        val recyclerView = root.findViewById(android.R.id.list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        contentSubscription = resourceObservable!!
                .flatMap { story -> apiClient.comments.get(projectId, story.id) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(({ comments -> adapter.setResources(comments) }))

        return root
    }

    override fun onDestroyView() {
        contentSubscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(projectId: Long): StoryCommentsFragment {
            val args = Bundle(1)
            args.putLong("projectId", projectId)

            val fragment = StoryCommentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
