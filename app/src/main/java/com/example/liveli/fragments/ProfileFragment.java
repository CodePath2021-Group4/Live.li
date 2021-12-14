package com.example.liveli.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.liveli.BuildConfig;
import com.example.liveli.ChannelAdapter;
import com.example.liveli.LoginActivity;
import com.example.liveli.R;
import com.example.liveli.models.Channel;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final String CHANNEL_URL =
            "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&%smaxResults=25&key=" + BuildConfig.YOUTUBE_KEY;

    List<Channel> channels;

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

        channels = new ArrayList<>();
        RecyclerView rvChannels = view.findViewById(R.id.rvChannels);

        ChannelAdapter channelAdapter = new ChannelAdapter(getContext(), channels);

        rvChannels.setAdapter(channelAdapter);
        rvChannels.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ParseUser currentUser = ParseUser.getCurrentUser();
        ImageView ivPostImage = view.findViewById(R.id.ivProfilePic);

        loadProfileImage(ivPostImage, currentUser);

        loadChannelsFollowed(channels, channelAdapter, currentUser);

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
                    if (getContext() != null) {
                        Glide.with(getContext()).load(objects.get(0).getImage().getUrl()).circleCrop().into(ivPostImage);
                    }
                }
            }
        });
    }

    private void loadChannelsFollowed(List<Channel> channels, ChannelAdapter channelAdapter, ParseUser currentUser) {
        List<String> urls = new ArrayList<>();
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, ParseException e) {
                if ( e != null) {
                    Log.e(TAG, "Error Loading Channels Followed", e);
                } else {
                    Log.i(TAG, "Great success " + objects.get(0).getJSONArray("channels_followed"));

                    JSONArray channel_array = objects.get(0).getJSONArray("channels_followed");

                    if (channel_array == null) {
                        Toast.makeText(getActivity(),"Empty Feed, follow some channels", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    String combination="";
                    for (int i=0; i<channel_array.length(); i++) {
                        try {
                            combination = combination + "id=" + channel_array.getString(i) + "&";
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(String.format(CHANNEL_URL, combination), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            JSONObject jsonObject = json.jsonObject;
                            try {
                                JSONArray items = jsonObject.getJSONArray("items");
                                channels.addAll(Channel.fromChannelArray(items));
                                channelAdapter.notifyDataSetChanged();

                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "onFailure");
                            Toast.makeText(getActivity(),"Empty Feed, follow some channels", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}