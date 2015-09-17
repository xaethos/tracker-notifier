package net.xaethos.trackernotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Me;
import net.xaethos.trackernotifier.subscribers.ErrorToastSubscriber;
import net.xaethos.trackernotifier.utils.PrefUtils;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    private String mToken;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToken = PrefUtils.getPrefs(this).getString(PrefUtils.PREF_TOKEN, null);

        if (mToken == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        } else {
            mSubscription = subscribeViews();
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode != RESULT_OK) {
                finish();
                return;
            }
            mToken = PrefUtils.getPrefs(this).getString(PrefUtils.PREF_TOKEN, null);
            subscribeViews();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Subscription subscribeViews() {
        final TextView nameView = (TextView) findViewById(R.id.text_name);
        final TextView initialsView = (TextView) findViewById(R.id.text_initials);

        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.user()
                .me(mToken)
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
