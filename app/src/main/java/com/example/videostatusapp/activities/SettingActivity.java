package com.example.videostatusapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.R;
import com.example.videostatusapp.SettingClickListner;
import com.example.videostatusapp.adapters.AdapterSetting;
import com.example.videostatusapp.adapters.CategoryListAdapter;
import com.example.videostatusapp.models.ModelSetting;
import com.example.videostatusapp.models.RecyclerCategoryModel;

import java.util.ArrayList;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class SettingActivity extends AppCompatActivity implements SettingClickListner {

    private RecyclerView recyclerView;
    private AdapterSetting adapterSetting;
    ArrayList<ModelSetting> modelSettingArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager parentLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        AdManager.getInstance(this).showBannerAd(this);
        modelSettingArrayList.add(new ModelSetting("Invite Friends",R.drawable.ic_baseline_favorite_active));
        modelSettingArrayList.add(new ModelSetting("Rate App",R.drawable.ic_baseline_star_rate_24));
        modelSettingArrayList.add(new ModelSetting("Like On Facebook",R.drawable.ic_facebook_logo));
        modelSettingArrayList.add(new ModelSetting("Follow On Instagram",R.drawable.ic_instagram));
        modelSettingArrayList.add(new ModelSetting("Privacy Policy",R.drawable.ic_baseline_privacy));
        modelSettingArrayList.add(new ModelSetting("Contact Us",R.drawable.ic_baseline_email_24));

        recyclerView = findViewById(R.id.recycler_setting);
        recyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(SettingActivity.this);
        adapterSetting = new AdapterSetting(modelSettingArrayList, SettingActivity.this);
        recyclerView.setLayoutManager(parentLayoutManager);
        recyclerView.setAdapter(adapterSetting);
        adapterSetting.notifyDataSetChanged();
    }

    public void backSetting(View view) {

        finish();
    }

    @Override
    public void clickedSetting(View view, int position) {
        if (position == 0){

            Intent share1 = new Intent(Intent.ACTION_SEND);
            share1.setType("text/plain");
            share1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share1.putExtra(Intent.EXTRA_SUBJECT, "Video Status App");
            share1.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=developer+name"+ getPackageName());
            startActivity(share1);
        }
        if (position == 1){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }
        if (position == 2){
            startActivity(getOpenFacebookIntent());
        }
        if (position == 3){

            Uri uri = Uri.parse("https://www.instagram.com/ishalvarigal_official/");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/ishalvarigal_official/")));
            }
        }
        if (position == 4){
            String url = "https://ishalvarigal.com/app-privacy-policy/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (position == 5){
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"byaribeats@gmail.com"});
            startActivity(emailIntent);
        }
    }
    public Intent getOpenFacebookIntent() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/426253597411506"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ishalvarigal"));
        }
    }
}