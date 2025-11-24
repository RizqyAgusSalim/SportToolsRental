package com.example.sporttoolsrental;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "SportRentalSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_NIM = "userNim";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Create login session
    public void createLoginSession(String id, String name, String email, String role, String nim) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_NIM, nim);
        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get user role
    public String getUserRole() {
        return preferences.getString(KEY_USER_ROLE, "");
    }

    // Get user name
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    // Get user email
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    // Get user NIM (for mahasiswa)
    public String getUserNim() {
        return preferences.getString(KEY_USER_NIM, "");
    }

    // Logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
}