package com.example.liveli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Profile extends AppCompatActivity {
    public static final String TAG = "ProfileFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ImageView ivPostImage = findViewById(R.id.ivProfilePic);

        loadProfileImage(ivPostImage, currentUser);

        TextView tvProfileName = findViewById(R.id.tvUsername);
        tvProfileName.setText(currentUser.getUsername());

        ImageButton ibExit =  findViewById(R.id.ibExit);
        ibExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ParseUser.getCurrentUser() != null) {
                    ParseUser.logOut();

                    logout();
                }
            }
        });
    }

    private void logout() {
        Intent i =  new Intent(Profile.this, LoginActivity.class);
        startActivity(i);
    }

    //--user defined
    private void loadProfileImage(ImageView ivPostImage, ParseUser currentUser) {
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, ParseException e) {
                if ( e != null) {
                    Log.e(TAG, "Error Loading Profile Image", e);
                } else {
                    Glide.with(Profile.this).load(objects.get(0).getImage().getUrl()).into(ivPostImage);
                }
            }
        });
    }
}