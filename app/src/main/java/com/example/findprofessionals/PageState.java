package com.example.findprofessionals;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class PageState {

    private final String ACTIVITY = "activity";
    private final String LAST_ACTIVITY = "last_activity";
    private final String USER_JSON = "user_json";

    private final SharedPreferences preferences;
    private final Gson gson = new Gson();
    public PageState(Context context) {
        String PREF_NAME = "PageState";
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Save last opened Activity

    public void saveActivity(String activity) {
        preferences.edit().putString(ACTIVITY, activity).apply();
    }
    public String getActivity() {
        return preferences.getString(ACTIVITY, null);
    }

    public void saveLastActivity(String last_activity) {
        preferences.edit().putString(LAST_ACTIVITY, last_activity).apply();
    }
    public String getLastActivity() { return preferences.getString(LAST_ACTIVITY, null); }

    public void saveUser(User user) {
        preferences.edit().putString(USER_JSON, gson.toJson(user)).apply();
    }
    public User getUser() {
        String json = preferences.getString(USER_JSON, null);
        if (json == null) return null;
        try {
            return gson.fromJson(json, User.class);
        } catch (Exception e) {
            return null;
        }
    }




    public void Clear() {
        preferences.edit().clear().apply();
    }
    public void clearUser() {
        preferences.edit().remove(USER_JSON).apply();
    }
}