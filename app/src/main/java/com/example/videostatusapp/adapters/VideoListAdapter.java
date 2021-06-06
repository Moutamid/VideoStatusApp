package com.example.videostatusapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.activities.MainActivity;
import com.example.videostatusapp.activities.SplashActivity;
import com.example.videostatusapp.activities.VideoPlayActivity;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.RecyclerCategoryModel;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder> {
    public ArrayList<ModelVideoList> childModelArrayList;
    public ArrayList<ModelVideoList> childModelArrayList2;
    Context cxt;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView videoImage;
        public TextView videoName;
        public  View viewItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.video_image);
            videoName = itemView.findViewById(R.id.video_name);
            viewItem=itemView;

        }
    }

    public VideoListAdapter(ArrayList<ModelVideoList> arrayList, Context mContext) {
        this.cxt = mContext;
        this.childModelArrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_videos_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ModelVideoList currentItem = childModelArrayList.get(position);
        holder.videoImage.setImageResource(currentItem.getVideo_image());
        holder.videoName.setText(currentItem.getVideo_name());
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(cxt)
                .load(currentItem.getUrl())
                .apply(requestOptions)
                .thumbnail(Glide.with(cxt).load(currentItem.getUrl()))
                .into(holder.videoImage);


//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//// Set video url as data source
//        retriever.setDataSource(currentItem.getUrl(), new HashMap<String, String>());
//// Get frame at 2nd second as Bitmap image
//        Bitmap bitmap = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//// Display the Bitmap image in an ImageView
//        holder.videoImage.setImageBitmap(bitmap);
        holder.viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cxt, VideoPlayActivity.class);
                intent.putExtra("category",currentItem.getCategory());
                intent.putExtra("name",currentItem.getVideo_name());
                intent.putExtra("url",currentItem.getUrl());
                cxt.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }

}
