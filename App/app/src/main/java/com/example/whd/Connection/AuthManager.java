package com.example.whd.Connection;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "WHD_Prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static AuthManager instance; // Statische Instanz
    private SharedPreferences prefs;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Methode zum Initialisieren (einmalig in der MainActivity/App-Start)
    public static void init(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
    }

    // Zugriff auf die Instanz
    public static AuthManager getInstance() {
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() { // WICHTIG: Zum Ausloggen!
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}