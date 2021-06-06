package com.example.videostatusapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.adapters.CategoryListAdapter;
import com.example.videostatusapp.adapters.VideoListAdapter;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.RecyclerCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouriteVideos extends AppCompatActivity {
    private RecyclerView listRecyclerView;
    private RecyclerView.Adapter listAdapter;
    ArrayList<ModelVideoList> recyclerListModelArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager listLayoutManager;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mAuth.signInAnonymously().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FavouriteVideos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_videos);
        AdManager.getInstance(this).showBannerAd(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        DatabaseReference mDatabaseRef;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Favourite")
                .child(mAuth.getCurrentUser().getUid());;
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists())
                    return;

                ArrayList<ModelVideoList> arrayList = new ArrayList<>();
                for (DataSnapshot child: snapshot.getChildren()) {

                    String videoname= child.child("Name").getValue().toString();
                    String url= child.child("Url").getValue().toString();
                    arrayList.add(new ModelVideoList(R.drawable.image_item,videoname,url,"Favourite"));
                }

                listRecyclerView = findViewById(R.id.recyclerview_list_favourite);
                listRecyclerView.setHasFixedSize(true);
                listLayoutManager = new GridLayoutManager(FavouriteVideos.this,2);
                VideoListAdapter childRecyclerViewAdapter = new VideoListAdapter(arrayList,FavouriteVideos.this);
                listRecyclerView.setAdapter(childRecyclerViewAdapter);
                listRecyclerView.setLayoutManager(listLayoutManager);
                childRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void backFavourite(View view) {
        finish();
    }
}