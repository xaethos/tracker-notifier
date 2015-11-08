package net.xaethos.trackernotifier.subscribers

import android.content.Context
import android.widget.Toast

import rx.Subscriber
import rx.subscriptions.Subscriptions

abstract class ErrorToastSubscriber<T>(context: Context) : Subscriber<T>() {

    internal var context: Context? = context

    init {
        add(Subscriptions.create { this.context = null })
    }

    override fun onError(error: Throwable) {
        context?.toastError(error)
    }

    override fun onCompleted() {
        //noop
    }
}

fun Context.toastError(error: Throwable) =
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
