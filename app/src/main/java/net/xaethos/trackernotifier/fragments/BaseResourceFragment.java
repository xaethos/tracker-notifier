package net.xaethos.trackernotifier.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import net.xaethos.trackernotifier.models.Resource;

import rx.Observable;

public abstract class BaseResourceFragment<T extends Resource> extends Fragment {
    private Observable<T> mResourceObservable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResourceSource) {
            mResourceObservable = ((ResourceSource) context).getResourceObservable();
        }
    }

    protected Observable<T> observeResource() {
        return mResourceObservable;
    }

    public interface ResourceSource<R extends Resource> {
        Observable<R> getResourceObservable();
    }
}
