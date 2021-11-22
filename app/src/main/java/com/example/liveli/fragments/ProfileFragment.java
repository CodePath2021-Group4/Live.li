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
import com.example.liveli.Profile;
import com.example.liveli.R;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                    Glide.with(getContext()).load(objects.get(0).getImage().getUrl()).into(ivPostImage);
                }
            }
        });
    }
}