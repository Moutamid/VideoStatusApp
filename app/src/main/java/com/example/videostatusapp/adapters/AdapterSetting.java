package com.example.videostatusapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostatusapp.R;
import com.example.videostatusapp.RecyclerClickListner;
import com.example.videostatusapp.SettingClickListner;
import com.example.videostatusapp.activities.AllVideoList;
import com.example.videostatusapp.activities.MainActivity;
import com.example.videostatusapp.models.ModelSetting;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.RecyclerCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.MyViewHolder> {
    private ArrayList<ModelSetting> settingArrayList;
    public Context cxt;
    private final SettingClickListner mListner;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public ImageView imageView;
        public  View view;


        public MyViewHolder(View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.name_setting);
            imageView = itemView.findViewById(R.id.icon_item);
            view= itemView;

        }
    }

    public AdapterSetting(ArrayList<ModelSetting> exampleList, Context context) {
        this.settingArrayList = exampleList;
        this.cxt = context;
        mListner = (SettingClickListner) context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return settingArrayList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ModelSetting currentItem = settingArrayList.get(position);

        holder.category.setText(currentItem.getName());
        holder.imageView.setImageResource(currentItem.getIcon());
        holder.view.setOnClickListener(v -> mListner.clickedSetting(v,holder.getAdapterPosition()));


    }


}
