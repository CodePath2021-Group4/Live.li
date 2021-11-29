package com.example.liveli;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liveli.models.Stream;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends YouTubeBaseActivity {
    public static final String TAG = "DetailActivity";

    YouTubePlayerView player;
    ImageView ivChannelImage;
    TextView tvStreamTitle;
    TextView tvChannelName;
    TextView tvStreamPublishedAt;
    TextView tvStreamDescription;
    DateFormat dateFormat;

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

        Stream stream = Parcels.unwrap(getIntent().getParcelableExtra("stream"));

        tvStreamTitle.setText(stream.getTitle());
        tvChannelName.setText(stream.getChannelName());
        tvStreamDescription.setText(stream.getDescription());

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
                youTubePlayer.cueVideo(stream.getVideoId());
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
}