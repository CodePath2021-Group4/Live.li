package com.example.liveli.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class Stream {

    public static final String TAG = "Stream";

    // Can be grabbed from JSON
    JSONObject snippet;
    JSONObject id;
    JSONObject thumbnails;
    JSONObject highQ;
    String channel_name;
    String title;
    String description;
    String publishedAt;
    String channel_id;
    String thumbnail;
    String video_id;

    // Need to be grabbed from specific vid search
    String channel_image;
    String view_count;


    public Stream(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getJSONObject("id");
        snippet = jsonObject.getJSONObject("snippet");
        thumbnails = snippet.getJSONObject("thumbnails");
        highQ = thumbnails.getJSONObject("high");


        channel_name = snippet.getString("channelTitle");
        title = snippet.getString("title");
        description = snippet.getString("description");
        publishedAt = snippet.getString("publishedAt");
        channel_id = snippet.getString("channelId");
        thumbnail = highQ.getString("url");
        video_id = id.getString("videoId");
        view_count = "";
        channel_image = "";


    }

    public static List<Stream> fromJsonArray (JSONArray streamJsonArray) throws JSONException {
        List<Stream> streams = new ArrayList<>();
        for (int i = 0; i < streamJsonArray.length(); i++) {
            streams.add(new Stream(streamJsonArray.getJSONObject(i)));
        }
        return streams;
    }


    public String getChannelName() {
        return channel_name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getChannelId() {
        return channel_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideoId() {
        return video_id;
    }

    public String getChannelImage() {
        return channel_image;
    }

    public String getViewCount() {
        return view_count;
    }

    public String setViewCount(String s) {
        view_count = s;
        return view_count;
    }

    public String setChannelImage(String s) {
        channel_image = s;
        return channel_image;
    }

}
