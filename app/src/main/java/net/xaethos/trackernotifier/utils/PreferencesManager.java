package net.xaethos.trackernotifier.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    public static final String PREF_TOKEN = "TrackerToken";

    private static PreferencesManager sInstance;

    private final SharedPreferences mPreferences;

    private PreferencesManager(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public static PreferencesManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context.getSharedPreferences("auth_preferences",
                    Context.MODE_PRIVATE));
        }
        return sInstance;
    }

    public String getTrackerToken() {
        return mPreferences.getString(PREF_TOKEN, null);
    }

    public boolean hasTrackerToken() {
        return mPreferences.contains(PREF_TOKEN);
    }

    public void setTrackerToken(String token) {
        final SharedPreferences.Editor edit = mPreferences.edit();
        if (token == null) {
            edit.remove(PREF_TOKEN);
        } else {
            edit.putString(PREF_TOKEN, token);
        }
        edit.apply();
    }

}
