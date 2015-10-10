package net.xaethos.trackernotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.xaethos.trackernotifier.utils.PrefUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PrefUtils.getPrefs(this).contains(PrefUtils.PREF_TOKEN)) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode != RESULT_OK) {
                finish();
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
            PrefUtils.getPrefs(this).edit().remove(PrefUtils.PREF_TOKEN).apply();
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
