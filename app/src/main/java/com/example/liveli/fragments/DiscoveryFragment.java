package com.example.liveli.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.liveli.BuildConfig;
import com.example.liveli.R;
import com.example.liveli.StreamAdapter;
import com.example.liveli.models.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;


public class DiscoveryFragment extends Fragment {

    public static final String TAG = "MainActivity";
    public static final String SEARCH_URL =
            "https://youtube.googleapis.com/youtube/v3/search?part=snippet&eventType=live&maxResults=25&order=viewCount&type=video&key=" + BuildConfig.YOUTUBE_KEY;
    public static final String VIDEO_URL =
            "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&part=liveStreamingDetails&%skey=" + BuildConfig.YOUTUBE_KEY;
    public static final String CHANNEL_URL =
            "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&%smaxResults=25&key=" + BuildConfig.YOUTUBE_KEY;

    List<Stream> streams;
    List<String> views;
    List<String> channels;
    HashMap<String, String> hmap;

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvStreams = view.findViewById(R.id.rvStreams);
        streams = new ArrayList<>();
        views = new ArrayList<>();
        channels = new ArrayList<>();
        hmap = new HashMap<String, String>();

        StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);

        rvStreams.setAdapter(streamAdapter);

        rvStreams.setLayoutManager(new LinearLayoutManager(getContext()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(SEARCH_URL, new JsonHttpResponseHandler()  {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(int statusCode, okhttp3.Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray items = jsonObject.getJSONArray("items");
                    Log.i(TAG, "Items: " + items.length());
                    streams.addAll(Stream.fromJsonArray(items));
                    streamAdapter.notifyDataSetChanged();

                    for (int i = 0; i < streams.size(); i++) {
                        views.add(streams.get(i).getVideoId());
                        channels.add(streams.get(i).getChannelId());
                    }

                    Log.i(TAG, "VIEW LIST: " + views.toString());
                    String combination="";
                    for (int i = 0; i < views.size(); i++) {
                        combination = combination + "id=" + views.get(i) + "&";
                    }

                    client.get(String.format(VIDEO_URL,combination), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG,"onSuccess2");
                            JSONObject jsonObject = json.jsonObject;
                            try {
                                JSONArray items = jsonObject.getJSONArray("items");
                                for (int i = 0; i < views.size(); i++) {
                                    JSONObject initial = items.getJSONObject(i);
                                    JSONObject details = initial.getJSONObject("liveStreamingDetails");
                                    streams.get(i).setViewCount(details.getString("concurrentViewers"));
                                }
                                streamAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "onFailure2");
                        }
                    });

                    combination="";
                    for (int i = 0; i < channels.size(); i++) {
                        combination = combination + "id=" + channels.get(i) + "&";
                    }
                    client.get(String.format(CHANNEL_URL, combination), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG,"onSuccess3");
                            JSONObject jsonObject = json.jsonObject;
                            try {
                                JSONArray items = jsonObject.getJSONArray("items");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject initial = items.getJSONObject(i);
                                    String id = initial.getString("id");
                                    JSONObject snippet = initial.getJSONObject("snippet");
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    JSONObject hiQ = thumbnails.getJSONObject("high");
                                    String url = hiQ.getString("url");
                                    hmap.put(id, url);
                                }
                                for (int i = 0; i < channels.size(); i++) {
                                    String avatar_url = hmap.get(streams.get(i).getChannelId());
                                    streams.get(i).setChannelImage(avatar_url);
                                }
                                Log.i(TAG, "HMAP: " + hmap.toString());
                                streamAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "onFailure3");
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}