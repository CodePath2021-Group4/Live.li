package com.example.liveli;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.liveli.models.Stream;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.ViewHolder>{

    Context context;
    List<Stream> streams;

    public StreamAdapter(Context context, List<Stream> streams) {
        this.context = context;
        this.streams = streams;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View streamView = LayoutInflater.from(context).inflate(R.layout.item_stream, parent, false);
        return new ViewHolder(streamView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stream stream = streams.get(position);
        holder.bind(stream);
    }

    @Override
    public int getItemCount() { return streams.size(); }

    // Clean all elements of the recycler
    public void clear() {
        streams.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Stream> list) {
        streams.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle;
        TextView tvName;
        ImageView ivThumbnail;
        TextView tvViews;
        TextView tvPostTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvName = itemView.findViewById(R.id.tvName);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);
            container = itemView.findViewById(R.id.shimmer_item);
        }

        public void bind(Stream stream) {
            tvTitle.setText(stream.getTitle());
            tvName.setText(stream.getChannelName());
            if (stream.getViewCount() == ""){
                tvViews.setText("Updating...");
            }
            else {
                tvViews.setText(stream.getViewCount() + " views");
            }

            Date date = null;
            try {
                DateFormat dateFormat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                }
                date = dateFormat.parse(stream.getPublishedAt());
                tvPostTime.setText(getFormattedTimeStamp(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Glide.with(context)
                    .load(stream.getThumbnail())
                    .into(ivThumbnail);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, stream.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("stream", Parcels.wrap(stream));
                    context.startActivity(i);
                }
            });
        }
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
