package com.example.whd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.whd.Connection.AuthManager;
import com.example.whd.Fragments.FragmentAccount;
import com.example.whd.Fragments.FragmentDashboard;
import com.example.whd.Fragments.ScheduleActivity.FragmentSchedule;
import com.example.whd.Fragments.ListActivity.FragmentList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    // Private Attributes
    private BottomNavigationView gui_BottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthManager.init(this);

        // LogIn-Test
        if (!AuthManager.getInstance().isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Forcing Portrait-Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Forcing LightMode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Init Gui-Display
        this.gui_BottomNavigationView = this.findViewById(R.id.gui_BottomNavigationView);
        this.gui_BottomNavigationView.setOnItemSelectedListener(this.gui_Listener_BottomNavigationView_Listener);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.gui_FrameLayout, new FragmentDashboard()).commit();
    }

    // Private Support-Listener
    private NavigationBarView.OnItemSelectedListener gui_Listener_BottomNavigationView_Listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            int getItemID = menuItem.getItemId();

            if (getItemID == R.id.gui_Page_Dashboard) {
                selectedFragment = new FragmentDashboard();
            } else if (getItemID == R.id.gui_Page_List) {
                selectedFragment = new FragmentList();
            } else if (getItemID == R.id.gui_Page_Schedule) {
                selectedFragment = new FragmentSchedule();
            } else if (getItemID == R.id.gui_Page_Account) {
                selectedFragment = new FragmentAccount();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.gui_FrameLayout, selectedFragment).commit();
            return true;
        }
    };



}