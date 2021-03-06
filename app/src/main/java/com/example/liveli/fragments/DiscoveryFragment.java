package com.example.liveli.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.liveli.BuildConfig;
import com.example.liveli.R;
import com.example.liveli.StreamAdapter;
import com.example.liveli.models.Stream;
import com.facebook.shimmer.ShimmerFrameLayout;

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
            "https://youtube.googleapis.com/youtube/v3/search?part=snippet&eventType=live&maxResults=25&order=viewCount&type=video%s%skey=" + BuildConfig.YOUTUBE_KEY;
    public static final String VIDEO_URL =
            "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&part=liveStreamingDetails&%skey=" + BuildConfig.YOUTUBE_KEY;
    public static final String CHANNEL_URL =
            "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&%smaxResults=25&key=" + BuildConfig.YOUTUBE_KEY;

    SwipeRefreshLayout swipeContainer;
    RecyclerView rvStreams;
    ShimmerFrameLayout shimmerFrameLayout;
    List<Stream> streams;
    List<String> views;
    List<String> channels;
    HashMap<String, String> hmap;
    String category;
    String query;
    EditText etInput;
    ImageButton btnSearch;

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

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        rvStreams = view.findViewById(R.id.rvStreams);


        streams = new ArrayList<>();
        category = "&";
        query = "&";


        StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);

        rvStreams.setAdapter(streamAdapter);

        rvStreams.setLayoutManager(new LinearLayoutManager(getContext()));


        etInput = view.findViewById(R.id.etInput);
        btnSearch = view.findViewById(R.id.btnSearch);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                streams = new ArrayList<>();
                StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);
                rvStreams.setAdapter(streamAdapter);
                rvStreams.setLayoutManager(new LinearLayoutManager(getContext()));
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                retrieveData(streams, streamAdapter, SEARCH_URL, category, query);
            }
        });

        Spinner dropdown = view.findViewById(R.id.sDropdown);
        String[] items = new String[]{"Popular", "Music", "Gaming", "Sports", "News"};
        ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(sAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        category = "&";
                        break;
                    case 1:
                        category = "&videoCategoryId=10&";
                        break;
                    case 2:
                        category = "&videoCategoryId=20&";
                        break;
                    case 3:
                        category = "&videoCategoryId=17&";
                        break;
                    case 4:
                        category = "&videoCategoryId=25&";
                        break;
                }
                Log.i(TAG, "category is " + category);
                streams = new ArrayList<>();
                StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);
                rvStreams.setAdapter(streamAdapter);
                rvStreams.setLayoutManager(new LinearLayoutManager(getContext()));
                retrieveData(streams, streamAdapter, SEARCH_URL, category, query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = "&";
                retrieveData(streams, streamAdapter, SEARCH_URL, category, query);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etInput.getText().toString().isEmpty()) {
                    query = "&";
                }
                else {
                    query = "&q=" + etInput.getText().toString() + "&";
                    streams = new ArrayList<>();
                    StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);
                    rvStreams.setAdapter(streamAdapter);
                    rvStreams.setLayoutManager(new LinearLayoutManager(getContext()));
                    retrieveData(streams, streamAdapter, SEARCH_URL, category, query);
                }
            }
        });

    }

    public void retrieveData(List<Stream> streams, StreamAdapter streamAdapter, String SEARCH_URL, String category, String query){
        views = new ArrayList<>();
        channels = new ArrayList<>();
        hmap = new HashMap<String, String>();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(SEARCH_URL, category, query), new JsonHttpResponseHandler()  {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(int statusCode, okhttp3.Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray items = jsonObject.getJSONArray("items");
                    Log.i(TAG, "Items: " + items.length());
                    streamAdapter.clear();
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

                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                rvStreams.setVisibility(View.VISIBLE);
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
                    swipeContainer.setRefreshing(false);

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
                Log.i(TAG, "URL" + SEARCH_URL);
                Log.e(TAG, "err", throwable);
            }
        });
    }
}