package net.xaethos.trackernotifier.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.adapters.NotificationsDataSource
import net.xaethos.trackernotifier.adapters.NotificationsDividerDecorator
import net.xaethos.trackernotifier.adapters.ResourceAdapter
import net.xaethos.trackernotifier.api.TrackerClient
import net.xaethos.trackernotifier.models.Notification
import net.xaethos.trackernotifier.models.Resource
import net.xaethos.trackernotifier.utils.Notifications
import net.xaethos.trackernotifier.utils.animateVisible
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.MultipleAssignmentSubscription

class NotificationsFragment : BaseAdapterFragment() {

    private val apiClient: TrackerClient = TrackerClient.getInstance()
    private val adapter: ResourceAdapter<Resource, NotificationsDataSource> = NotificationsDataSource.createAdapter()

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
        dataSubscription.set(subscribeDataSource(adapter.dataSource))
    }

    private fun subscribeDataSource(dataSource: NotificationsDataSource): Subscription {
        refreshView?.isRefreshing = true
        val emptyView = emptyView
        setEmptyText(R.string.msg_fetching_content, 0)

        return apiClient.notifications.get()
                .flatMap { Observable.from(it) }
                .filter { notification -> notification.read_at == null }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { refreshView?.isRefreshing = false }
                .subscribe({ notification ->
                    dataSource.addNotification(notification)
                    if (emptyView != null) animateVisible(emptyView, false)
                }, { error ->
                    setEmptyText("Error", error.message)
                }, {
                    if (emptyView != null) {
                        if (dataSource.itemCount == 0) {
                            setEmptyText(R.string.headline_no_notifications,
                                    R.string.msg_pull_to_refresh)
                            animateVisible(emptyView, true)
                        } else {
                            animateVisible(emptyView, false)
                        }
                    }
                })
    }

    private fun markAsRead(notifications: List<Notification>) {
        val container = view ?: return
        val context = container.context

        val count = notifications.size
        val message = context.resources.getQuantityString(R.plurals.toast_notifications_read, count, count)

        val dataSource = adapter.dataSource
        val readItems = Observable.from(notifications)

        Snackbar.make(container, message, Snackbar.LENGTH_LONG).setCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar, event: Int) {
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) return

                // User didn't undo the swipe, so actually mark notifications read
                readItems.forEach { notification ->
                    Notifications.markRead(apiClient, notification.id)
                            .subscribe({ n -> Log.i("XAE", "notification read: " + n.id) },
                                    { error ->
                                        Log.d("XAE", "markRead error", error)
                                        dataSource.addNotification(notification)
                                    })
                }
            }
        }).setAction(R.string.action_undo) { readItems.subscribe { dataSource.addNotification(it) } }.show()
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

            val removed = adapter.dataSource.removeItem(position)
            markAsRead(removed)
        }
    }

}
