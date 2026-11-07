package com.example.findprofessionals;

import android.content.Context;
import android.content.SharedPreferences;

public class PageState {

    private static final String PREF_NAME = "PageState";
    private static final String LAST_ACTIVITY = "last_activity";
    private final SharedPreferences preferences;

    public PageState(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Save last opened Activity
    public void SaveActivity(String activity) {
        preferences.edit()
                .putString(LAST_ACTIVITY, activity)
                .apply();
    }

    public String GetActivity() {
        return preferences.getString(LAST_ACTIVITY, "ThereNoPage");
    }

    // Clear saved navigation state
    public void Clear() {
        preferences.edit().clear().apply();
    }
}