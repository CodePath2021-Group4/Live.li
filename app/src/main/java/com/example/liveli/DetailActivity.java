package com.example.liveli;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.liveli.models.Channel;
import com.example.liveli.models.Stream;
import com.example.liveli.parseobjects.UserProfile;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
    public static final String TAG = "DetailActivity";

    YouTubePlayerView player;
    ImageView ivChannelImage;
    TextView tvStreamTitle;
    TextView tvChannelName;
    TextView tvStreamPublishedAt;
    TextView tvStreamDescription;
    DateFormat dateFormat;
    Button btnFollow;
    String currentChannel;

    private boolean buttonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        player = findViewById(R.id.player);
        ivChannelImage = findViewById(R.id.ivChannelImage);
        tvStreamTitle = findViewById(R.id.tvStreamTitle);
        tvChannelName = findViewById(R.id.tvChannelName);
        tvStreamPublishedAt = findViewById(R.id.tvStreamPublishedAt);
        ivChannelImage = findViewById(R.id.ivChannelImage);
        tvStreamDescription = findViewById(R.id.tvStreamDescription);
        btnFollow = (Button) findViewById(R.id.btnFollow);

        Stream stream = Parcels.unwrap(getIntent().getParcelableExtra("stream"));

        tvStreamTitle.setText(stream.getTitle());
        tvChannelName.setText(stream.getChannelName());
        tvStreamDescription.setText(stream.getDescription());
        currentChannel = stream.getChannelId();



        checkIfFollowed(ParseUser.getCurrentUser(), currentChannel, buttonClicked);

        btnFollow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 Log.i(TAG, "Is followed? : " + isFollowed);
                 buttonClicked = true;
                 if(checkIfFollowed(ParseUser.getCurrentUser(), currentChannel, buttonClicked).equals("FOLLOW")) {
                     addChannel(ParseUser.getCurrentUser(), currentChannel);
                     btnFollow.setText("UNFOLLOW");
                     btnFollow.setBackgroundColor(Color.parseColor("#555555"));
                     Log.i(TAG, "Button followed!!!!!!!!!!!!!!!!!!!!!");
                 } else {
                     removeChannel(ParseUser.getCurrentUser(), currentChannel);
                     btnFollow.setText("FOLLOW");
                     btnFollow.setBackgroundColor(Color.parseColor("#ffff6c00"));
                     Log.i(TAG, "Button followed!!!!!!!!!!!!!!!!!!!!!");
//
                 }


             }
        });

        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse( stream.getPublishedAt() );
            tvStreamPublishedAt.setText(getFormattedTimeStamp(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        player.initialize(BuildConfig.YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(stream.getVideoId());
                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        if ( b ){
                            youTubePlayer.play();
                        }
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG, "Failed To Initialized Player");
                Log.e(TAG, youTubeInitializationResult.toString());
            }
        });

        Glide.with(DetailActivity.this).load(stream.getChannelImage()).circleCrop().into(ivChannelImage);
    }

    private String getFormattedTimeStamp(Date createdAt) {
        Date currentDate = new Date();
        Log.i("TimeNow" , currentDate.toString());
        Log.i("TimeAT" , createdAt.toString());
        float seconds = (float) Math.floor((currentDate.getTime() - createdAt.getTime()) / 1000);

        float interval = (seconds / 31536000);

        if (interval > 1) {
            return (int) Math.floor(interval) + " year(s) ago";
        }
        interval = seconds / 2592000;
        if (interval > 1) {
            return (int)Math.floor(interval) + " month(s) ago";
        }
        interval = seconds / 86400;
        if (interval > 1) {
            return (int)Math.floor(interval) + " day(s) ago";
        }
        interval = seconds / 3600;
        if (interval > 1) {
            return (int)Math.floor(interval) + " hour(s) ago";
        }
        interval = seconds / 60;
        if (interval > 1) {
            return (int)Math.floor(interval) + " minute(s) ago";
        }
        return (int)Math.floor(seconds) + " second(s) ago";
    }


    public void addChannel(ParseUser currentUser, String currentChannel) {
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error Loading Channels Followed", e);
                } else {
                    Log.i(TAG, "Great success " + objects.get(0).getJSONArray("channels_followed"));

                    JSONArray channel_array = objects.get(0).getJSONArray("channels_followed");
                    List<String> updatedChannels = new ArrayList<>();
                    for (int i = 0; i < channel_array.length(); i++) {  // iterate through the JsonArray
                        try {
                            String channel = channel_array.getString(i);
                            updatedChannels.add(channel);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                    updatedChannels.add(currentChannel);
                    objects.get(0).put("channels_followed", updatedChannels);
                    objects.get(0).saveInBackground();
                }
            }
        });
    }

    public void removeChannel(ParseUser currentUser, String currentChannel) {
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error Loading Channels Followed", e);
                } else {
                    Log.i(TAG, "Great success " + objects.get(0).getJSONArray("channels_followed"));

                    JSONArray channel_array = objects.get(0).getJSONArray("channels_followed");
                    List<String> updatedChannels = new ArrayList<>();
                    for (int i = 0; i < channel_array.length(); i++) {  // iterate through the JsonArray
                        try {
                            String channel = channel_array.getString(i);
                            if(!channel.equals(currentChannel))
                                updatedChannels.add(channel);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                    objects.get(0).put("channels_followed", updatedChannels);
                    objects.get(0).saveInBackground();
                }
            }
        });
    }

    public String checkIfFollowed(ParseUser currentUser, String currentChannel, boolean buttonClicked) {
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, com.parse.ParseException e) {
                if ( e != null) {
                    Log.e(TAG, "Error Loading Channels Followed", e);
                } else {
                    Log.i(TAG, "Great success " + objects.get(0).getJSONArray("channels_followed"));

                    JSONArray channel_array = objects.get(0).getJSONArray("channels_followed");

                    for(int i = 0; i < channel_array.length(); i++) {  // iterate through the JsonArray
                        try {
                            String channel = channel_array.getString(i);
                            Log.i(TAG, "Button's text: " + btnFollow.getText());
                            // if enter detail screen and
                            if (currentChannel.equals(channel) && btnFollow.getText().equals("FOLLOW") && !buttonClicked) {
                                btnFollow.setText("UNFOLLOW");
                                btnFollow.setBackgroundColor(Color.parseColor("#555555"));
                                Log.i(TAG,"MATCH! ---------------------------------------------");
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }

                    if (channel_array == null) {
                        Toast.makeText(getApplicationContext(),"Empty Feed, follow some channels", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }
        });
        return (String) btnFollow.getText();
    }
}