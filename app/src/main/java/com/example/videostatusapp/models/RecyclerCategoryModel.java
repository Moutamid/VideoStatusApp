package com.example.videostatusapp.models;

import java.util.ArrayList;
import java.util.List;

public class RecyclerCategoryModel {
    String categoryName;
    ArrayList<ModelVideoList> modelVideoLists;

    public RecyclerCategoryModel(String categoryName, ArrayList<ModelVideoList> modelVideoLists) {
        this.categoryName = categoryName;
        this.modelVideoLists = modelVideoLists;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<ModelVideoList> getModelVideoLists() {
        return modelVideoLists;
    }

    public void setModelVideoLists(ArrayList<ModelVideoList> modelVideoLists) {
        this.modelVideoLists = modelVideoLists;
    }
}
