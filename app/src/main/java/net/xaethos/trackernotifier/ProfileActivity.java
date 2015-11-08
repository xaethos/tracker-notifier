package net.xaethos.trackernotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Me;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.MultipleAssignmentSubscription;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    private TrackerClient mClient;
    private MultipleAssignmentSubscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSubscription = new MultipleAssignmentSubscription();
        mClient = TrackerClient.Companion.getInstance();
        if (mClient.hasToken()) {
            mSubscription.set(subscribeViews());
        } else {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        }
    }

    @Override
    protected void onDestroy() {
        mSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode != RESULT_OK) {
                finish();
                return;
            }
            mSubscription.set(subscribeViews());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Subscription subscribeViews() {
        final TextView nameView = (TextView) findViewById(R.id.text_name);
        final TextView initialsView = (TextView) findViewById(R.id.text_initials);

        return mClient.getMe()
                .get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorToastSubscriber<Me>(this) {
                    @Override
                    public void onNext(Me me) {
                        nameView.setText(me.name);
                        initialsView.setText(me.initials);
                    }
                });
    }
}
