package net.xaethos.trackernotifier.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    public static final String PREF_TOKEN = "TrackerToken";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
    }

}
