package com.example.videostatusapp.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.RecyclerClickListner;
import com.example.videostatusapp.activities.AllVideoList;
import com.example.videostatusapp.models.VideoList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    List<VideoList> videoLists;
    Context context;
    ViewPager2 viewPager;
    VideoList videoList;
    private final RecyclerClickListner mListner;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public VideoAdapter(List<VideoList> videoLists, Context context, ViewPager2 viewPager) {
        this.videoLists = videoLists;
        this.context = context;
        this.viewPager = viewPager;
        mListner = (RecyclerClickListner) context;
    }


    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_play_item, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, final int position) {
        videoList = videoLists.get(position);
        holder.videoView.setVideoPath(videoList.getVideo_url());

//TODO: ADJUST FAVOURITE STRATEGY

        if (mAuth.getCurrentUser()!=null) {
            DatabaseReference mDatabaseRef;
            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Favourite")
                    .child(mAuth.getCurrentUser().getUid());
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists())
                        return;

                    int count = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {

                        String videoname = child.child("Name").getValue().toString();
                        if (videoname.equals(videoList.getVideo_name())) {

                            count++;
                        }
                    }
                    if (count > 0) {
                        holder.favourite.setImageResource(R.drawable.ic_baseline_favorite_active);
                        holder.favourite.setEnabled(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.progressBar.setVisibility(View.VISIBLE);
        if (position>1 && position%5==0){
            AdManager.getInstance(context).showInterstitialAd(context);
        }
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
                // final int mytime=mp.getDuration();


                holder.videoView.requestFocus();
                holder.progressBar.setVisibility(View.GONE);
                //  new Handler().postDelayed(new Runnable() {
                //  @Override
                //  public void run() {
                //     viewPager.setCurrentItem(position+1,true);
                //     notifyDataSetChanged();
                // }
                // },mytime);


                mp.setLooping(true);
                float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
                float scale = videoRatio / screenRatio;
                if (scale >= 1f) {
                    holder.videoView.setScaleX(scale);
                } else {
                    holder.videoView.setScaleY(1f / scale);
                }
            }

        });

        holder.download.setOnClickListener(v -> mListner.clickedDownload(v, holder.getAdapterPosition(), videoList.getVideo_name(), videoList.getVideo_url()));
        holder.favourite.setOnClickListener(v -> {
            mListner.clickedFavourite(v, holder.getAdapterPosition(), videoList.getVideo_name(), videoList.getVideo_url());
            holder.favourite.setImageResource(R.drawable.ic_baseline_favorite_active);
        });

    }

    @Override
    public int getItemCount() {
        return videoLists.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        ImageView download, favourite;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            download = itemView.findViewById(R.id.download_bt);
            favourite = itemView.findViewById(R.id.favourite_bt);

        }
    }


}
