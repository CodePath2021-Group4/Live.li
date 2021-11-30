package com.example.liveli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.liveli.fragments.DiscoveryFragment;
import com.example.liveli.fragments.PersonalFeedFragment;
import com.example.liveli.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private String currentScreen = "discovery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.action_discovery:
                        fragment = new DiscoveryFragment();
                        if (currentScreen.equals("personal")) {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        if (currentScreen.equals("profile")) {
                            transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        Toast.makeText(MainActivity.this, "Discovery!", Toast.LENGTH_SHORT).show();
                        currentScreen = "discovery";
                        break;
                    case R.id.action_personal_feed:
                        fragment = new PersonalFeedFragment();
                        if (currentScreen.equals("discovery")) {
                            transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        if (currentScreen.equals("profile")) {
                            transaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
                        }
                        Toast.makeText(MainActivity.this, "Personal Feed!", Toast.LENGTH_SHORT).show();
                        currentScreen = "personal";
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        if (currentScreen.equals("personal")) {
                            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        if (currentScreen.equals("discovery")) {
                            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        currentScreen = "profile";
                        break;
                    default:
                        fragment = new DiscoveryFragment();
                        Toast.makeText(MainActivity.this, "Default!", Toast.LENGTH_SHORT).show();
                        break;
                }

                transaction.replace(R.id.flContainer, fragment).commit();
                return true;

            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_discovery);
    }
}