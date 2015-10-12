package net.xaethos.trackernotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.fragments.NotificationsFragment;
import net.xaethos.trackernotifier.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    NotificationsFragment mNotificationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationsFragment =
                (NotificationsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (!TrackerClient.getInstance().hasToken()) {
            PreferencesManager prefs = PreferencesManager.getInstance(this);
            if (!prefs.hasTrackerToken()) {
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            } else {
                TrackerClient.getInstance().setToken(prefs.getTrackerToken());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode != RESULT_OK) {
                finish();
            } else {
                mNotificationsFragment.refresh();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_logout:
            TrackerClient.getInstance().setToken(null);
            PreferencesManager.getInstance(this).setTrackerToken(null);
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
