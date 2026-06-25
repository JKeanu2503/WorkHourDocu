package com.example.whd;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static final String PREF_NAME = "whd_prefs"; // Name deiner Datei
    private static PrefManager instance;
    private SharedPreferences sharedPreferences;

    // Privater Konstruktor
    private PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Zugriffsmethode
    public static synchronized PrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // Speichern
    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    // Lesen
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Beispiel für zusätzliche Datentypen
    public void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
}