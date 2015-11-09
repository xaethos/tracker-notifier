package net.xaethos.trackernotifier.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.adapters.CommentViewHolder
import net.xaethos.trackernotifier.adapters.ResourceAdapter
import net.xaethos.trackernotifier.adapters.SimpleDividerDecorator
import net.xaethos.trackernotifier.api.TrackerClient
import net.xaethos.trackernotifier.models.Story
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class StoryCommentsFragment : BaseAdapterFragment() {
    protected var storyObservable: Observable<Story>? = null
        private set


    private val apiClient = TrackerClient.instance

    override protected val adapter = ResourceAdapter(R.layout.item_comment) { inflater, viewType, parent ->
        CommentViewHolder(inflater.inflate(viewType, parent, false))
    }

    private var projectId: Long = 0
    private var contentSubscription: Subscription? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        storyObservable = (context as? BaseResourceFragment.ResourceSource<Story>)?.resourceObservable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments.getLong("projectId")
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(SimpleDividerDecorator(context))
        recyclerView?.adapter = adapter

        contentSubscription = subscribeComments()
    }

    override fun onDestroyView() {
        contentSubscription?.unsubscribe()
        super.onDestroyView()
    }

    private fun subscribeComments(): Subscription {
        setEmptyText(R.string.msg_fetching_content, 0)
        return (storyObservable ?: Observable.empty<Story>())
                .take(1)
                .flatMap { story -> apiClient.comments.get(projectId, story.id, ":default,person(name,username,initials)") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ comments -> adapter.setResources(comments) },
                        { error -> setEmptyText(getString(R.string.headline_error), error.message) },
                        { setEmptyText(R.string.headline_no_comments, 0) })
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
