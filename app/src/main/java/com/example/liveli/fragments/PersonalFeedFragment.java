package com.example.liveli.fragments;

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

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.liveli.BuildConfig;
import com.example.liveli.Profile;
import com.example.liveli.R;
import com.example.liveli.StreamAdapter;
import com.example.liveli.models.Stream;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFeedFragment extends Fragment {
    public static final String TAG = "PersonalFeedFragment";

    private static final String SEARCH_URL = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelId=%s&eventType=live&maxResults=25&type=video&key=" + BuildConfig.YOUTUBE_KEY;
    public static final String VIDEO_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&part=liveStreamingDetails&%skey=" + BuildConfig.YOUTUBE_KEY;
    public static final String CHANNEL_URL = "https://youtube.googleapis.com/youtube/v3/channels?part=snippet&%smaxResults=25&key=" + BuildConfig.YOUTUBE_KEY;

    List<Stream> streams;
    List<String> views;
    List<String> channels;
    HashMap<String, String> hmap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonalFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFeedFragment newInstance(String param1, String param2) {
        PersonalFeedFragment fragment = new PersonalFeedFragment();
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
        return inflater.inflate(R.layout.fragment_personal_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //--//
        streams = new ArrayList<>();
        views = new ArrayList<>();
        channels = new ArrayList<>();
        hmap = new HashMap<String, String>();

        StreamAdapter streamAdapter = new StreamAdapter(getContext(), streams);

        RecyclerView rvPersonalStreams = view.findViewById(R.id.rvPersonalStreams);
        rvPersonalStreams.setAdapter(streamAdapter);
        rvPersonalStreams.setLayoutManager(new LinearLayoutManager(getContext()));

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
        query.whereEqualTo(UserProfile.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<UserProfile>() {
            @Override
            public void done(List<UserProfile> objects, ParseException e) {
                if ( e != null) {
                    Log.e(TAG, "Error Loading Profile Image", e);
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    JSONArray followed = objects.get(0).getFollowed();

                    //Log.i(TAG, "Profile Lookup Was Good!");
                    //Log.i(TAG, String.valueOf(followed.length()));
                    for (int x = 0; x < followed.length(); x++){
                        try {
                            Log.i(TAG, followed.getString(x));
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }

                    String searchFollowedUrl;
                    try {
                        searchFollowedUrl = String.format(SEARCH_URL, followed.getString(2));
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                        searchFollowedUrl = String.format(SEARCH_URL, "UCBi2mrWuNuyYy4gbM6fU18Q");
                    }

                    client.get(searchFollowedUrl, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            JSONObject jsonObject = json.jsonObject;

                            try {
                                JSONArray items = jsonObject.getJSONArray("items");
                                streams.addAll(Stream.fromJsonArray(items));
                                streamAdapter.notifyDataSetChanged();

                                for (int i = 0; i < streams.size(); i++) {
                                    views.add(streams.get(i).getVideoId());
                                    channels.add(streams.get(i).getChannelId());
                                }

                                //GET video data and extract view count
                                StringBuilder vidIdParams = new StringBuilder();
                                for (int i = 0; i < views.size(); i++) {
                                    vidIdParams.append("id=").append(views.get(i)).append("&");
                                }

                                String videoFollowedUrl = String.format(VIDEO_URL, vidIdParams.toString().toString());
                                client.get(videoFollowedUrl, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                                        //JSONObject jsonObject = json.jsonObject;
                                        try {
                                            JSONArray items = json.jsonObject.getJSONArray("items");

                                            for (int i = 0; i < views.size(); i++) {
                                                String views = items.getJSONObject(i).getJSONObject("liveStreamingDetails").getString("concurrentViewers");
                                                streams.get(i).setViewCount(views);
                                            }
                                        } catch (JSONException jsonException) {
                                            jsonException.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                        Log.e(TAG, "Error Getting Viewer Count");
                                    }
                                });

                                //GET channel data and extract channel image
                                StringBuilder channelIdParams = new StringBuilder();
                                for (int i = 0; i < channels.size(); i++) {
                                    channelIdParams.append("id=").append(channels.get(i)).append("&");
                                }

                                String channelFollowedUrl = String.format(CHANNEL_URL, channelIdParams);
                                client.get(channelFollowedUrl, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                                        try {
                                            JSONArray items = json.jsonObject.getJSONArray("items");
                                            for (int i = 0; i < items.length(); i++) {
                                                String channelId = items.getJSONObject(i).getString("id");
                                                String url = items.getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
                                                hmap.put(channelId, url);
                                            }
                                        } catch (JSONException jsonException) {
                                            jsonException.printStackTrace();
                                        }

                                        for (int i = 0; i < channels.size(); i++) {
                                            streams.get(i).setChannelImage(hmap.get(streams.get(i).getChannelId()));
                                        }

                                        streamAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                        Log.e(TAG, "Error Getting Channel Image URL");
                                    }
                                });


                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }
            }
        });
    }
}