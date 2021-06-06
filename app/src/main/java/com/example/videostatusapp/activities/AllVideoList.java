package com.example.videostatusapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.adapters.VideoListAdapter;
import com.example.videostatusapp.models.ModelVideoList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllVideoList extends AppCompatActivity {

    private RecyclerView listRecyclerView;
    private TextView tvCategory;
    private RecyclerView.Adapter listAdapter;
    ArrayList<ModelVideoList> recyclerListModelArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager listLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_video_list);
        AdManager.getInstance(this).showBannerAd(this);
        tvCategory=findViewById(R.id.category_name_list);
        ArrayList<ModelVideoList> arrayList = new ArrayList<>();
        Intent intent=getIntent();
        String key=intent.getStringExtra("category");
        tvCategory.setText(key);
        DatabaseReference newref = FirebaseDatabase.getInstance().getReference().child("Videos").child(key);
//
        newref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ModelVideoList> arrayList = new ArrayList<>();
                for (DataSnapshot child: snapshot.getChildren()) {

                    String videoname= child.child("Name").getValue().toString();
                    String url= child.child("Url").getValue().toString();
                    arrayList.add(new ModelVideoList(R.drawable.image_item,videoname,url,key));
                }

                listRecyclerView = findViewById(R.id.recyclerview_list);
                listRecyclerView.setHasFixedSize(true);
                listLayoutManager = new GridLayoutManager(AllVideoList.this,2);
                VideoListAdapter childRecyclerViewAdapter = new VideoListAdapter(arrayList,AllVideoList.this);
                listRecyclerView.setAdapter(childRecyclerViewAdapter);
                listRecyclerView.setLayoutManager(listLayoutManager);
                childRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void backAllVideo(View view) {
        finish();
        AdManager.getInstance(AllVideoList.this).showInterstitialAd(AllVideoList.this);
    }
}