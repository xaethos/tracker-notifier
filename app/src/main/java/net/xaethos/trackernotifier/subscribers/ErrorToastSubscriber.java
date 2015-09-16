package net.xaethos.trackernotifier.subscribers;

import android.content.Context;
import android.widget.Toast;

import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public abstract class ErrorToastSubscriber<T> extends Subscriber<T> {

    Context mContext;

    public ErrorToastSubscriber(Context context) {
        mContext = context;
        add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                mContext = null;
            }
        }));
    }

    @Override
    public void onError(Throwable error) {
        Context context = mContext;
        if (context != null) Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCompleted() {
        //noop
    }
}