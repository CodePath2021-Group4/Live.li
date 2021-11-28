package com.example.liveli;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liveli.models.Stream;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ImageView ivChannelImage;
    TextView tvStreamTitle;
    TextView tvChannelName;
    TextView tvStreamPublishedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivChannelImage = findViewById(R.id.ivChannelImage);
        tvStreamTitle = findViewById(R.id.tvStreamTitle);
        tvChannelName = findViewById(R.id.tvChannelName);
        tvStreamPublishedAt = findViewById(R.id.tvStreamPublishedAt);

        Stream stream = Parcels.unwrap(getIntent().getParcelableExtra("stream"));

        tvStreamTitle.setText(stream.getTitle());
        tvChannelName.setText(stream.getChannelName());
        tvStreamPublishedAt.setText(stream.getPublishedAt());
    }
}