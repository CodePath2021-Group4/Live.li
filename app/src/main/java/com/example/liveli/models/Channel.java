package com.example.liveli.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    public static final String TAG = "Channel";

    // Can be grabbed from JSON
    String channel_id;
    String channel_name;
    String channel_image;

    public Channel(JSONObject jsonObject) throws JSONException {
        JSONObject snippet;
        JSONObject thumbnails;
        JSONObject hiQ;

        snippet = jsonObject.getJSONObject("snippet");
        thumbnails = snippet.getJSONObject("thumbnails");
        hiQ = thumbnails.getJSONObject("high");

        channel_id = jsonObject.getString("id");
        channel_name = snippet.getString("title");
        channel_image = hiQ.getString("url");
    }

    public static List<Channel> fromChannelArray (JSONArray channelJsonArray) throws JSONException {
        List<Channel> channels = new ArrayList<>();
        for (int i = 0; i < channelJsonArray.length(); i++) {
            channels.add(new Channel(channelJsonArray.getJSONObject(i)));
        }
        return channels;
    }
    public String getFollowedChannelId() {
        return channel_id;
    }
    public String getFollowedChannelName() {
        return channel_name;
    }
    public String getFollowedChannelImage() {
        return channel_image;
    }
}
