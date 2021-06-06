package com.example.videostatusapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.adapters.CategoryListAdapter;
import com.example.videostatusapp.adapters.VideoListAdapter;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.RecyclerCategoryModel;
import com.example.videostatusapp.models.VideoList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static boolean isPurchased;
    private RecyclerView parentRecyclerView;
    private CategoryListAdapter ParentAdapter;
    ArrayList<RecyclerCategoryModel> recyclerCategoryModelArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager parentLayoutManager;
    SearchView searchView;

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
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.search_view);
        if (!isNetworkConnected()){
            Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
        AdManager.getInstance(this).showBannerAd(this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Videos");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
//

                            DatabaseReference newref = FirebaseDatabase.getInstance().getReference().child("Videos").child(key);
//
                            newref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<ModelVideoList> arrayList = new ArrayList<>();
                                    for (DataSnapshot child : snapshot.getChildren()) {

                                        String videoname = child.child("Name").getValue().toString();
                                        String url = child.child("Url").getValue().toString();
                                        arrayList.add(new ModelVideoList(R.drawable.image_item, videoname, url, key));
                                    }
                                    recyclerCategoryModelArrayList.add(new RecyclerCategoryModel(key, arrayList));

                                    parentRecyclerView = findViewById(R.id.recycler_main);
                                    parentRecyclerView.setHasFixedSize(true);
                                    parentLayoutManager = new LinearLayoutManager(MainActivity.this);
                                    ParentAdapter = new CategoryListAdapter(recyclerCategoryModelArrayList, MainActivity.this);
                                    parentRecyclerView.setLayoutManager(parentLayoutManager);
                                    parentRecyclerView.setAdapter(ParentAdapter);
                                    ParentAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ParentAdapter.getFilter().filter("");

//                ParentAdapter.filterVideo("");
//                Toast.makeText(MainActivity.this, "Closed", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

//                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();

                //ParentAdapter.dataFilter(query);

//                ParentAdapter.filterVideo(query);
                ParentAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //ParentAdapter.dataFilter(newText);


                return false;
            }
        });
    }

    public void openFavaourite(View view) {
        Intent intent = new Intent(MainActivity.this, FavouriteVideos.class);
        startActivity(intent);
        AdManager.getInstance(MainActivity.this).showInterstitialAd(MainActivity.this);
    }

    public void btSetting(View view) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
        AdManager.getInstance(MainActivity.this).showInterstitialAd(MainActivity.this);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}