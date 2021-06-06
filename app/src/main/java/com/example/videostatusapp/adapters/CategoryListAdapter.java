package com.example.videostatusapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.activities.AllVideoList;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.RecyclerCategoryModel;
import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "CategoryListAdapter";

    private ArrayList<RecyclerCategoryModel> parentModelArrayList = new ArrayList<>();
    private final ArrayList<RecyclerCategoryModel> parentModelArrayList2 = new ArrayList<>();
    VideoListAdapter childRecyclerViewAdapter;
    public Context cxt;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category, seeAllVideo;
        public RecyclerView childRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category_name);
            seeAllVideo = itemView.findViewById(R.id.see_all_videos);
            childRecyclerView = itemView.findViewById(R.id.recyclerView_video);
        }
    }

    public CategoryListAdapter(ArrayList<RecyclerCategoryModel> exampleList, Context context) {
        this.parentModelArrayList = exampleList;
        this.parentModelArrayList2.addAll(exampleList);
        this.cxt = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return parentModelArrayList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RecyclerCategoryModel currentItem = parentModelArrayList.get(position);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setHasFixedSize(true);

        holder.seeAllVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cxt, AllVideoList.class);
                intent.putExtra("category", currentItem.getCategoryName());
                cxt.startActivity(intent);
                AdManager.getInstance(cxt).showInterstitialAd(cxt);
            }
        });
        holder.category.setText(currentItem.getCategoryName());

        childRecyclerViewAdapter = new VideoListAdapter(currentItem.getModelVideoLists(), holder.childRecyclerView.getContext());
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);

    }

    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: ");
        return shopsFilter;
    }

    private Filter shopsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RecyclerCategoryModel> filteredList = new ArrayList<>();
            Log.d(TAG, "performFiltering: ");
            if (constraint == null
                    || constraint.length() == 0
                    || constraint.toString().trim().equals("")
                    || constraint.toString() == null)
            {
                Log.d(TAG, "performFiltering: if (constraint == null || constraint.length() == 0 || constraint.toString().trim().equals(\"\")) {");
                filteredList.addAll(parentModelArrayList2);
            } else {
                Log.d(TAG, "performFiltering: } else {");
                String filterPattern = constraint.toString().toLowerCase();

//                for (Property item : mDataList) {
//                    if (item.getFullAddress() != null)
//                        if (item.getFullAddress().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(item);
//                        }
//                }

                for (int i = 0; i <= parentModelArrayList.size() - 1; i++) {
                    ArrayList<ModelVideoList> modelVideoLists = parentModelArrayList.get(i)
                            .getModelVideoLists();

                    for (int i2 = 0; i2 <= modelVideoLists.size() - 1; i2++) {

                        if (modelVideoLists.get(i2).getVideo_name().toLowerCase().contains(filterPattern)) {
                            filteredList.add(parentModelArrayList.get(i));
//                            modelVideoLists.remove(i2);
                        }

                    }

                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            if (mData == null) {
//                Log.d(TAG, "publishResults: if (mData == null) {");
//                return;
//            }
//            if (results.values == null) {
//                Log.d(TAG, "publishResults: if ( results.values == null){");
//                return;
//            }
            parentModelArrayList.clear();
//            mData = new ArrayList<>();
//            mData.clear();
//            mData = (ArrayList<Property>) results.values;
            parentModelArrayList.addAll((ArrayList<RecyclerCategoryModel>) results.values);
            notifyDataSetChanged();
            Log.d(TAG, "publishResults: done");
        }
    };

    public void filterVideo(String filterValue) {

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(cxt);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (filterValue.equals("") || TextUtils.isEmpty(filterValue)) {
            this.parentModelArrayList.clear();
            this.parentModelArrayList.addAll(this.parentModelArrayList2);
            notifyDataSetChanged();
            progressDialog.dismiss();
            return;
        }

        for (int i = 0; i <= this.parentModelArrayList.size() - 1; i++) {
            ArrayList<ModelVideoList> modelVideoLists = this.parentModelArrayList.get(i)
                    .getModelVideoLists();

            for (int i2 = 0; i2 <= modelVideoLists.size() - 1; i2++) {

                if (!modelVideoLists.get(i2).getVideo_name().contains(filterValue)) {
                    modelVideoLists.remove(i2);
                }

            }

        }
        progressDialog.dismiss();
        notifyDataSetChanged();

        this.parentModelArrayList.clear();
        this.parentModelArrayList.addAll(this.parentModelArrayList2);
    }

}
