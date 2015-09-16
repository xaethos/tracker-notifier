package net.xaethos.trackernotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.xaethos.trackernotifier.utils.PrefUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToken = PrefUtils.getPrefs(this).getString(PrefUtils.PREF_TOKEN, null);
        if (mToken == null) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode != RESULT_OK) {
                finish();
                return;
            }
            mToken = PrefUtils.getPrefs(this).getString(PrefUtils.PREF_TOKEN, null);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public String getToken() {
        return mToken;
    }
}
