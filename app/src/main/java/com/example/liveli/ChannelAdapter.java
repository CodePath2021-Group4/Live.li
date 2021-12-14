package com.example.liveli;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.liveli.models.Channel;
import com.example.liveli.parseobjects.UserProfile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    Context context;
    List<Channel> channels;

    public ChannelAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View channelView = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ChannelAdapter.ViewHolder(channelView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Channel channel = channels.get(position);
        holder.bind(channel);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Channel itemRemoved = channels.get(position);
                String removed_id = itemRemoved.getFollowedChannelId();
                channels.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseQuery<UserProfile> query = ParseQuery.getQuery(UserProfile.class);
                query.whereEqualTo(UserProfile.KEY_USER, currentUser);
                query.findInBackground(new FindCallback<UserProfile>() {
                    @Override
                    public void done(List<UserProfile> objects, ParseException e) {
                        if ( e != null) {
                            Log.e("ChannelAdapter", "Error Loading Profile Image", e);
                        }
                        else {
                            JSONArray channel_array = objects.get(0).getJSONArray("channels_followed");
                            List<String> updatedChannels = new ArrayList<>();
                            for (int i=0; i<channel_array.length(); i++) {
                                try {
                                    String channel_id = channel_array.getString(i);
                                    if (!channel_id.equals(removed_id)) {
                                        updatedChannels.add(channel_id);
                                        Log.i("ChannelAdapter", "Channel Added"+ channel_id);
                                    }
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
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        ImageView ivChannel;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChannel = itemView.findViewById(R.id.ivChannel);
            container = itemView.findViewById(R.id.shimmer_item);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Channel channel) {
            Glide.with(context)
                    .load(channel.getFollowedChannelImage()).circleCrop()
                    .into(ivChannel);
        }
    }
}
