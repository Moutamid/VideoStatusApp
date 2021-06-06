package com.example.videostatusapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.utils.Utils;
import com.example.videostatusapp.AdManager;
import com.example.videostatusapp.BuildConfig;
import com.example.videostatusapp.R;
import com.example.videostatusapp.RecyclerClickListner;
import com.example.videostatusapp.adapters.VideoAdapter;
import com.example.videostatusapp.adapters.VideoListAdapter;
import com.example.videostatusapp.models.ModelVideoList;
import com.example.videostatusapp.models.VideoList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class VideoPlayActivity extends AppCompatActivity implements RecyclerClickListner {

    ViewPager2 viewPager;
    VideoAdapter adapter;
    VideoView videoView;

    private TextView currentBytesTextView;
    //    private TextView totalBytesTextView;
    private ProgressBar dialogProgressBar;

    private Dialog dialog;
    private int downloadId;

    final ArrayList<VideoList> downloadedList = new ArrayList<>();
    private String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 102;

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
                    Toast.makeText(VideoPlayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Intent intent = getIntent();
        String key = intent.getStringExtra("category");
        String name = intent.getStringExtra("name");
        String url = intent.getStringExtra("url");
        downloadedList.add(new VideoList(
                1,
                url,
                name));

        viewPager = findViewById(R.id.my_viewpager);

        videoView = findViewById(R.id.videoView);

        //for full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        DatabaseReference newref;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (key.equals("Favourite")) {
            newref = FirebaseDatabase.getInstance().getReference().child("Favourite")
                    .child(mAuth.getCurrentUser().getUid());
            ;
        } else {
            newref = FirebaseDatabase.getInstance().getReference().child("Videos").child(key);
        }
        newref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists())
                    return;

                for (DataSnapshot child : snapshot.getChildren()) {

                    String videoname = child.child("Name").getValue().toString();
                    String videourl = child.child("Url").getValue().toString();

                    if (!(url.equals(videourl))) {


                        downloadedList.add(new VideoList(
                                2,
                                videourl,
                                videoname));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new VideoAdapter(downloadedList, this, viewPager);
        viewPager.setAdapter(adapter);

    }

    private void showDownloadProgressDialog() {
        dialog = new Dialog(VideoPlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_downloading_progress);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        currentBytesTextView = dialog.findViewById(R.id.current_bytes_textview);
//        totalBytesTextView = dialog.findViewById(R.id.total_bytes_textview);
        dialogProgressBar = dialog.findViewById(R.id.progressBarOne);
        dialogProgressBar.setIndeterminate(true);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                PRDownloader.cancel(downloadId);
                dialogInterface.dismiss();
                Toast.makeText(VideoPlayActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    @Override
    public void clickedDownload(View view, int position, String videoname, String url) {
//        Toast.makeText(this, "videoname: " + videoname + " url: " + url, Toast.LENGTH_LONG).show();

        Dexter.withActivity(VideoPlayActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        Toast.makeText(VideoPlayActivity.this, "Permission granted successfully!", Toast.LENGTH_SHORT).show();
                        startDownloadingVideo(videoname, url);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            Toast.makeText(VideoPlayActivity.this, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void startDownloadingVideo(String videoname, String url) {

        showDownloadProgressDialog();

        final String fileNameStr = videoname + "-" + System.currentTimeMillis() + ".mp4";

        downloadId = PRDownloader.download(url, getFilePathString(), fileNameStr)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        dialogProgressBar.setIndeterminate(false);
//                        progressDialog.show();
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        dialogProgressBar.setProgress((int) progressPercent);
                        currentBytesTextView.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                        dialogProgressBar.setIndeterminate(false);

//                        progressDialog.setMax((int) progress.totalBytes);
//                        progressDialog.setProgress((int) progress.currentBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
//                        progressDialog.dismiss();
                        dialog.dismiss();
                        Toast t;
                        t = Toast.makeText(VideoPlayActivity.this, "Saved to Downloads", Toast.LENGTH_SHORT);
                        t.show();
//                        Toast.makeText(VideoPlayActivity.this, "", Toast.LENGTH_SHORT).show();

                        shareVideoUri(fileNameStr);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(VideoPlayActivity.this, error.getServerErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void shareVideoUri(String fileNameStr) {

        File fileWithinMyDir = new File(getFilePathString(), fileNameStr);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if (fileWithinMyDir.exists()) {
            Uri videoURI = FileProvider.getUriForFile(VideoPlayActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    fileWithinMyDir);

            intentShareFile.setType("video/mp4");
//            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + getFilePathString() + fileNameStr));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, videoURI);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    @Override
    public void clickedFavourite(View view, int position, String videoname, String url) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseRef;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Favourite")
                .child(mAuth.getCurrentUser().getUid())
                .push();
        mDatabaseRef.child("Name").setValue(videoname);
        mDatabaseRef.child("Url").setValue(url);
        Toast.makeText(this, "Add to Favourite", Toast.LENGTH_SHORT).show();

    }

    private String getFilePathString() {
//        String path_save_aud = "";
        String path_save_vid = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//             Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            + File.separator
//            path_save_aud =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                            + File.separator +
//                            getResources().getString(R.string.app_name) +
//                            File.separator + "audio";
            path_save_vid =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            File.separator +
                            getResources().getString(R.string.app_name) +
                            File.separator + "videos";

        } else {
//            path_save_aud =
//                    Environment.getExternalStorageDirectory().getAbsolutePath() +
//                            File.separator +
//                            getResources().getString(R.string.app_name) +
//                            File.separator + "audio";
            path_save_vid =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator +
                            getResources().getString(R.string.app_name) +
                            File.separator + "videos";

        }


        return path_save_vid;
//        final File newFile2 = new File(path_save_aud);
//        newFile2.mkdir();
//        newFile2.mkdirs();
//
//        final File newFile4 = new File(path_save_vid);
//        newFile4.mkdir();
//        newFile4.mkdirs();

    }

    @Override
    public void onBackPressed() {
        finish();
        AdManager.getInstance(this).showInterstitialAd(this);
    }
}