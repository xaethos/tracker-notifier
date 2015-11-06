package net.xaethos.trackernotifier.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.adapters.NotificationsAdapter
import net.xaethos.trackernotifier.adapters.NotificationsDividerDecorator
import net.xaethos.trackernotifier.api.TrackerClient
import net.xaethos.trackernotifier.models.Notification
import net.xaethos.trackernotifier.utils.Log
import net.xaethos.trackernotifier.utils.markRead
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.MultipleAssignmentSubscription

class NotificationsFragment : BaseAdapterFragment() {

    private val apiClient: TrackerClient = TrackerClient.instance
    protected override val adapter: NotificationsAdapter = NotificationsAdapter()

    private lateinit var dataSubscription: MultipleAssignmentSubscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        dataSubscription = MultipleAssignmentSubscription()
    }

    override fun onDestroy() {
        dataSubscription.unsubscribe()
        super.onDestroy()
    }

    override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(NotificationsDividerDecorator(context))
        recyclerView?.adapter = adapter
        ItemTouchHelper(SwipeCallback()).attachToRecyclerView(recyclerView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_notification, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> refresh()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun refresh() {
        dataSubscription.set(subscribeAdapter())
    }

    private fun subscribeAdapter(): Subscription {
        refreshView?.isRefreshing = true
        setEmptyText(R.string.msg_fetching_content, 0)

        return apiClient.notifications.get()
                .flatMap { Observable.from(it) }
                .filter { notification -> notification.read_at == null }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { refreshView?.isRefreshing = false }
                .subscribe({ notification -> adapter.addNotification(notification) },
                        { error -> setEmptyText(getString(R.string.headline_error), error.message) },
                        { setEmptyText(R.string.headline_no_notifications, R.string.msg_pull_to_refresh) })
    }

    private fun markAsRead(notifications: List<Notification>) {
        val container = view ?: return
        val context = container.context

        val count = notifications.size
        val message = context.resources.getQuantityString(R.plurals.toast_notifications_read, count, count)

        val readItems = Observable.from(notifications)

        Snackbar.make(container, message, Snackbar.LENGTH_LONG).setCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar, event: Int) {
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) return

                // User didn't undo the swipe, so actually mark notifications read
                readItems.forEach { notification ->
                    apiClient.notifications.markRead(notification.id)
                            .subscribe(
                                    { Log.i { "notification marked read: ${it.id}" } },
                                    { error ->
                                        Log.w(error) { "markRead error" }
                                        adapter.addNotification(notification)
                                    }
                            )
                }
            }
        }).setAction(R.string.action_undo) { readItems.subscribe { adapter.addNotification(it) } }.show()
    }

    private inner class SwipeCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            val removed = adapter.removeItem(position)
            markAsRead(removed)
        }
    }

}
