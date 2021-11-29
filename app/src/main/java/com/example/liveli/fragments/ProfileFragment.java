package com.example.liveli.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liveli.LoginActivity;
import com.example.liveli.R;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ParseUser currentUser = ParseUser.getCurrentUser();
        ImageView ivPostImage = view.findViewById(R.id.ivProfilePic);

        loadProfileImage(ivPostImage, currentUser);

        TextView tvProfileName = view.findViewById(R.id.tvUsername);
        tvProfileName.setText(currentUser.getUsername());

        ImageButton ibExit =  view.findViewById(R.id.ibExit);
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
        Intent i =  new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
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
                    Glide.with(getContext()).load(objects.get(0).getImage().getUrl()).circleCrop().into(ivPostImage);
                }
            }
        });
    }
}