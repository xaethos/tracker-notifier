package net.xaethos.trackernotifier.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import net.xaethos.trackernotifier.R
import net.xaethos.trackernotifier.utils.setTextOrHide

abstract class BaseAdapterFragment : Fragment() {

    protected var recyclerView: RecyclerView? = null
        private set
    protected var refreshView: SwipeRefreshLayout? = null
        private set

    protected var emptyView: View? = null
        private set
    private var headlineView: TextView? = null
    private var captionView: TextView? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view == null) return;

        recyclerView = view.findViewById(android.R.id.list) as? RecyclerView

        emptyView = view.findViewById(android.R.id.empty)
        headlineView = view.findViewById(R.id.headline) as? TextView
        captionView = view.findViewById(R.id.caption) as? TextView

        refreshView = view.findViewById(R.id.refresh) as? SwipeRefreshLayout
        refreshView?.setOnRefreshListener { this.refresh() }
    }

    override fun onDestroyView() {
        recyclerView = null
        refreshView = null
        emptyView = null
        headlineView = null
        captionView = null
        super.onDestroyView()
    }

    protected fun setEmptyText(headlineRes: Int, captionRes: Int) {
        headlineView?.setTextOrHide(headlineRes)
        captionView?.setTextOrHide(captionRes)
    }

    protected fun setEmptyText(headlineText: CharSequence?, captionText: CharSequence?) {
        headlineView?.setTextOrHide(headlineText)
        captionView?.setTextOrHide(captionText)
    }

    abstract fun refresh()

}