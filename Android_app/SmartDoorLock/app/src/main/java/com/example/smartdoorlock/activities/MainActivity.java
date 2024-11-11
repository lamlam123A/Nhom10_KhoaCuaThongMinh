package com.example.smartdoorlock.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.fragments.AboutFragment;
import com.example.smartdoorlock.fragments.AccessLogFragment;
import com.example.smartdoorlock.fragments.CameraStreamFragment;
import com.example.smartdoorlock.fragments.ChangeCardIdFragment;
import com.example.smartdoorlock.fragments.ChangePasswordFragment;
import com.example.smartdoorlock.fragments.HomeFragment;
import com.example.smartdoorlock.fragments.SettingsFragment;
import com.example.smartdoorlock.utils.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationHelper.createNotificationChannel(this);

        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.nav_view);

//        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        setupNavigationView();
        setupBottomNavigation();

        fab.setOnClickListener(v -> {
            // Kiểm tra xem CameraStreamFragment đã tồn tại chưa
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

            if (!(currentFragment instanceof CameraStreamFragment)) {
                fragmentManager.beginTransaction().replace(R.id.frame_layout, CameraStreamFragment.newInstance()).addToBackStack(null).commit();
            }
        });
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_changeCardID) {
                replaceFragment(new ChangeCardIdFragment());
            } else if (id == R.id.nav_changePassword) {
                replaceFragment(new ChangePasswordFragment());
            } else if (id == R.id.nav_about) {
                replaceFragment(new AboutFragment());
            } else if (id == R.id.nav_logout) {
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void setupBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_page) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.settings_page) {
                replaceFragment(new SettingsFragment());
            } else if (id == R.id.about_us_page) {
                replaceFragment(new AboutFragment());
            } else if (id == R.id.access_log_page) {
                replaceFragment(new AccessLogFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}