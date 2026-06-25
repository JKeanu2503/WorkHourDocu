package com.example.whd;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.AuthManager;
import com.example.whd.Connection.Models.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Private Attributes ==========================================================================

    // Private GUI-Elements ========================================================================
    private EditText gui_EditText_LogIn_Username;
    private EditText gui_EditText_LogIn_Password;
    private ImageButton gui_ImageButton_LogIn_Confirm;

    // Overridden Methods ==========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Forcing Portrait-Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Forcing LightMode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.gui_EditText_LogIn_Username = (EditText) findViewById(R.id.gui_EditText_LogIn_Username);
        this.gui_EditText_LogIn_Password = (EditText) findViewById(R.id.gui_EditText_LogIn_Password);
        this.gui_ImageButton_LogIn_Confirm = (ImageButton) findViewById(R.id.gui_ImageButton_LogIn_Confirm);

        this.gui_ImageButton_LogIn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    // Private Support-Methods =====================================================================
    private void performLogin() {
        String username = gui_EditText_LogIn_Username.getText().toString();
        String password = gui_EditText_LogIn_Password.getText().toString();

        // Validierung (schadet nie!)
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Bitte Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        // API Aufruf über ApiManager
        ApiManager.getInstance().getService().login(username, password).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 1. Token speichern
                    String token = response.body().getAccessToken();
                    AuthManager.getInstance().saveToken(token);

                    PrefManager.getInstance(LoginActivity.this).saveString("username", username);

                    // 2. Zur MainActivity wechseln
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // LoginActivity beenden
                } else {
                    Toast.makeText(LoginActivity.this, "LogIn fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Kein Fehler-Body";
                        Log.e("LOGIN_ERROR", "Code: " + response.code() + " | Fehler: " + errorMsg);
                        Toast.makeText(LoginActivity.this, "Fehler: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("LOGIN_ERROR", "Fehler beim Lesen der Fehlermeldung: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Verbindung zum Server fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}