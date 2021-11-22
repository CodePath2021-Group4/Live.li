package com.example.liveli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
                switch (menuItem.getItemId()) {
                    case R.id.action_discovery:
                        fragment = new DiscoveryFragment();
                        Toast.makeText(MainActivity.this, "Discovery!", Toast.LENGTH_SHORT).show();
//                        fragmentManager.beginTransaction().replace(R.id.flContainer, homeFragment);
                        break;
                    case R.id.action_personal_feed:
                        fragment = new PersonalFeedFragment();
                        Toast.makeText(MainActivity.this, "Personal Feed!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = new DiscoveryFragment();
                        Toast.makeText(MainActivity.this, "Default!", Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;

            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_discovery);
    }
}