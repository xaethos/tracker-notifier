package net.xaethos.trackernotifier.fragments

import android.content.Context
import android.support.v4.app.Fragment

import net.xaethos.trackernotifier.models.Resource

import rx.Observable

abstract class BaseResourceFragment<T : Resource> : Fragment() {
    protected var resourceObservable: Observable<T>? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        resourceObservable = (context as? ResourceSource<T>)?.resourceObservable
    }

    interface ResourceSource<R : Resource> {
        val resourceObservable: Observable<R>
    }
}
