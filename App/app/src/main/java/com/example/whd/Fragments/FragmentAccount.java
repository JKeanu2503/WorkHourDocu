package com.example.whd.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.PasswordUpdate;
import com.example.whd.Connection.Models.UserUpdate;
import com.example.whd.Connection.Models.UserUpdateResponse;
import com.example.whd.PrefManager;
import com.example.whd.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAccount extends Fragment {

    // Private Attributes ==========================================================================

    // Private GUI-Elements ========================================================================
    private EditText gui_EditText_Account_Username;
    private EditText gui_EditText_Account_OldPassword;
    private EditText gui_EditText_Account_NewPassword;
    private EditText gui_EditText_Account_ConfirmNewPassword;
    private ImageButton gui_ImageButton_Account_ConfirmUsername;
    private ImageButton gui_ImageButton_Account_ConfirmChangePassword;
    private ImageButton gui_ImageButton_Account_Logout;


    // Constructor =================================================================================
    public FragmentAccount() {
        this.gui_EditText_Account_Username = null;
        this.gui_EditText_Account_OldPassword = null;
        this.gui_EditText_Account_NewPassword = null;
        this.gui_EditText_Account_ConfirmNewPassword = null;
        this.gui_ImageButton_Account_ConfirmUsername = null;
        this.gui_ImageButton_Account_ConfirmChangePassword = null;
        this.gui_ImageButton_Account_Logout = null;
    }

    // Overridden Methods ==========================================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        this.gui_EditText_Account_Username = (EditText) view.findViewById(R.id.gui_EditText_Account_Username);
        this.gui_EditText_Account_OldPassword = (EditText) view.findViewById(R.id.gui_EditText_Account_ChangePW_OldPW);
        this.gui_EditText_Account_NewPassword = (EditText) view.findViewById(R.id.gui_EditText_Account_ChangePW_NewPW);
        this.gui_EditText_Account_ConfirmNewPassword = (EditText) view.findViewById(R.id.gui_EditText_Account_ChangePW_ConfirmNewPW);
        this.gui_ImageButton_Account_ConfirmUsername = (ImageButton) view.findViewById(R.id.gui_ImageButton_Account_UserNameConfirm);
        this.gui_ImageButton_Account_ConfirmChangePassword = (ImageButton) view.findViewById(R.id.gui_ImageButton_Account_ConfirmNewPassword);
        this.gui_ImageButton_Account_Logout = (ImageButton) view.findViewById(R.id.gui_ImageButton_Account_LogOut);

        String username = PrefManager.getInstance(requireContext()).getString("username", "");
        this.gui_EditText_Account_Username.setText(username);

        this.add_OnClickListener_to_GUIs();
        this.loadDataFromAPI();

        return view;
    }

    // Private Support-Methods =====================================================================
    private void loadDataFromAPI() {

    }

    private void add_OnClickListener_to_GUIs() {
        this.gui_ImageButton_Account_ConfirmUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = gui_EditText_Account_Username.getText().toString();
                UserUpdate userUpdate = new UserUpdate(username);
                ApiManager.getInstance().getService().updateUsername(userUpdate).enqueue(new Callback<UserUpdateResponse>() {
                    @Override
                    public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                        if (response.isSuccessful()) {
                            PrefManager.getInstance(getContext()).saveString("username", username);
                            Toast.makeText(getContext(), "Benutzername aktualisiert", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Fehler", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        this.gui_ImageButton_Account_ConfirmChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = gui_EditText_Account_OldPassword.getText().toString();
                String newPassword = gui_EditText_Account_NewPassword.getText().toString();
                String conPassword = gui_EditText_Account_ConfirmNewPassword.getText().toString();

                if (newPassword.length() > 0 && conPassword.equals(newPassword)) {
                    ApiManager.getInstance().getService().changePassword(new PasswordUpdate(oldPassword, newPassword)).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(getContext(), "Passwort aktualisiert", Toast.LENGTH_SHORT).show();
                            gui_EditText_Account_OldPassword.setText("");
                            gui_EditText_Account_NewPassword.setText("");
                            gui_EditText_Account_ConfirmNewPassword.setText("");
                            logout();
                            String errorJson = null;
                            try {
                                errorJson = response.errorBody().string();
                                Log.e("API_DEBUG", "FEHLER " + response.code() + ": " + errorJson);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Passwörter stimmen nicht", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.gui_ImageButton_Account_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout () {
        // 1. Token im AuthManager löschen
        com.example.whd.Connection.AuthManager.getInstance().clearToken();

        // 2. Prefs bereinigen (Optional, falls du User-Daten speicherst)
        PrefManager.getInstance(requireContext()).saveString("username", "");

        // 3. Zur Login-Activity navigieren
        Intent intent = new Intent(getActivity(), com.example.whd.LoginActivity.class);
        // Flags sorgen dafür, dass die MainActivity aus dem Backstack gelöscht wird
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}