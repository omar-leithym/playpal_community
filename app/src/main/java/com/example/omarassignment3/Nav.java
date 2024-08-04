// MainActivity.java
package com.example.omarassignment3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.LayoutInflater;
import android.view.View;

public class Nav extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private View bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.profile) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "User");

                Intent intent = new Intent(Nav.this, profilepage.class);
                intent.putExtra("email", email);

                startActivity(intent);



                //SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                //String email = sharedPreferences.getString("email", "User");
                //selectedFragment = new profilepage();
                //Bundle args = new Bundle();
                //args.putString("email", email);
                //selectedFragment.setArguments(args);
            } else if (itemId == R.id.chat) {
                selectedFragment = new ChatListFragment();
            } else if (itemId == R.id.notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Set the default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void hideNavigationBar() {
        if (bottomAppBar != null) {
            bottomAppBar.setVisibility(View.GONE);
        }
    }

    public void showNavigationBar() {
        if (bottomAppBar != null) {
            bottomAppBar.setVisibility(View.VISIBLE);
        }
    }

}
