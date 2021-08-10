package com.sagar.snaphub;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.cocosw.bottomsheet.BottomSheet;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.sagar.snaphub.HomeFragment.HomeFragment;
import com.sagar.snaphub.HomeFragment.HomeModel;
import com.sagar.snaphub.HomeFragment.HomePageAdapter;
import com.sagar.snaphub.HomeFragment.OnLikedCallback;
import com.sagar.snaphub.TrendingFragment.TrendingFragment;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.sagar.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;

import sagar.snaphub.BuildConfig;
import sagar.snaphub.R;


public class FullScreenActivity extends AppCompatActivity {

    private Intent intent;

    private ImageView expandBtn;
    private ImageView downloadBtn;
    private ImageView shareBtn;
    private ImageView setWallpaperBtn;
    private ImageView likeBtn;
    private TextView viewCountTextView;
    private TextView downloadsCountTextView;
    private TextView likesCountTextView;
    private TextView resolutionTextView;
    private TextView viewsHeadingTextView;
    private TextView downloadsHeadingTextView;
    private TextView resolutionHeadingTextView;
    private TextView likesHeadingTextView;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private AppBarLayout appBarLayout;
    private BigImageView bigImageView;

    private HomeModel photo;
    private int position;
    private String adapterName;

    private int downloadID;

    private Dialog progressDialog;
    private LottieAnimationView progressBar;


    private boolean isSetWallpaper = false;
    private boolean isShare = false;
    private String currentImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(this));
        setContentView(R.layout.activity_full_screen);


        ///assignment///
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        expandBtn = findViewById(R.id.expand_btn);
        downloadBtn = findViewById(R.id.download_btn);
        shareBtn = findViewById(R.id.share_btn);
        likeBtn = findViewById(R.id.spark_button);
        setWallpaperBtn = findViewById(R.id.set_wallpaper_btn);
        viewCountTextView = findViewById(R.id.views_count_textView);
        downloadsCountTextView = findViewById(R.id.downloads_count_textView);
        likesCountTextView = findViewById(R.id.likes_textview);
        resolutionTextView = findViewById(R.id.resolution_textview);
        viewsHeadingTextView = findViewById(R.id.views_heading_textView);
        downloadsHeadingTextView = findViewById(R.id.downloads_heading_textView);
        resolutionHeadingTextView = findViewById(R.id.resolution_heading_textView);
        likesHeadingTextView = findViewById(R.id.likes_heading_textView);
        bigImageView = findViewById(R.id.big_image);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();

        intent = getIntent();
        photo = (HomeModel) intent.getSerializableExtra("image_id");
        position = intent.getIntExtra("position", -1);
        adapterName = intent.getStringExtra("adapterName");
        ///assignment///

        ///////// ProgressDialog
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog_box);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    PRDownloader.cancel(downloadID);
                    progressDialog.dismiss();
                }
                return true;
            }
        });
        progressBar = progressDialog.findViewById(R.id.progressBar);
        ///////// ProgressDialog

        init(photo);

    }

    private void init(final HomeModel photo) {
        bigImageView.showImage(Uri.parse(photo.image_1080p));
        initViews();
        displayData();
        initPanel();

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageAdapter.toggleLike(getApplicationContext(), photo, likeBtn, false, new OnLikedCallback() {
                    @Override
                    public void onLiked() {
                        switch (adapterName) {
                            case "HOME_FRAGMENT":
                                HomeFragment.adapter.notifyItemChanged(position);
                                break;
                            case "TOP_RATED":
                                TrendingFragment.topAdapter.notifyItemChanged(position);
                                break;
                        }
                        displayData();
                    }
                });

            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSetWallpaper = false;
                isShare = false;

                downloadFile();
            }
        });

        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSetWallpaper = true;
                isShare = false;

                downloadFile();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShare = true;
                isSetWallpaper = false;

                downloadFile();
            }
        });
    }

    private void downloadFile() {
        if (checkPermission()) {
            new BottomSheet.Builder(FullScreenActivity.this).darkTheme().title("Select Resolution").sheet(R.menu.bottom_sheet_menu).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.res_4k:
                            downloadID = downloadFile(photo.image_4k);
                            break;
                        case R.id.res_1080p:
                            downloadID = downloadFile(photo.image_1080p);
                            break;
                        case R.id.res_720p:
                            downloadID = downloadFile(photo.image_720p);
                            break;
                        case R.id.res_480p:
                            downloadID = downloadFile(photo.image_480p);
                            break;
                    }
                }
            }).show();
        } else
            requestPermission();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap newOriginalBM = loadBitmap(resultUri);
                reloadWallpaper(newOriginalBM);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap loadBitmap(Uri src) {
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeStream(
                    getBaseContext().getContentResolver().openInputStream(src));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    private void reloadWallpaper(Bitmap bm) {
        if (bm != null) {
            WallpaperManager myWallpaperManager =
                    WallpaperManager.getInstance(getApplicationContext());

            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (myWallpaperManager.isWallpaperSupported()) {
                    try {
                        myWallpaperManager.setBitmap(bm);
                        Toast.makeText(this, "Wallpaper Successfully Changed!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FullScreenActivity.this,
                            "isWallpaperSupported() NOT SUPPORTED",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    myWallpaperManager.setBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(FullScreenActivity.this, "bm == null", Toast.LENGTH_LONG).show();
        }
    }

    private void initPanel() {
        ////////// SlidingPanel
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 1) {
                    AnimationSet animSet = new AnimationSet(true);
                    animSet.setInterpolator(new DecelerateInterpolator());
                    animSet.setFillAfter(true);
                    animSet.setFillEnabled(true);

                    final RotateAnimation animRotate = new RotateAnimation(0.0f, -180.0f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                    animRotate.setDuration(200);
                    animRotate.setFillAfter(true);
                    animSet.addAnimation(animRotate);
                    expandBtn.startAnimation(animRotate);
                } else if (slideOffset == 0) {
                    AnimationSet animSet = new AnimationSet(true);
                    animSet.setInterpolator(new DecelerateInterpolator());
                    animSet.setFillAfter(true);
                    animSet.setFillEnabled(true);

                    final RotateAnimation animRotate = new RotateAnimation(-180.0f, 0.0f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                    animRotate.setDuration(200);
                    animRotate.setFillAfter(true);
                    animSet.addAnimation(animRotate);
                    expandBtn.startAnimation(animRotate);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        ////////// SlidingPanel
    }

    private void displayData() {
        HomeFragment.firebaseFirestore.collection("HOME_FRAGMENT")
                .document(photo.photoId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            viewCountTextView.setText(String.valueOf((long) snapshot.get("views")));
                            downloadsCountTextView.setText(String.valueOf((long) snapshot.get("downloads")));
                            resolutionTextView.setText("4k");
                            likesCountTextView.setText(String.valueOf((long) snapshot.get("likes")));
                            if (photo.isLiked) {
                                likeBtn.setImageDrawable(getDrawable(R.drawable.heart_active));
                                likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFF0000")));
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private int downloadFile(String url) {
        progressBar.setMinAndMaxFrame(146, 246);
        progressBar.setFrame(146);
        progressDialog.show();
        String fileName = String.valueOf(generateRandomDigits(10)) + ".jpeg";
        String dirPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "PixHub";

        currentImagePath = dirPath + File.separator + fileName;

        return /*download id*/ PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int prog = (int) ((Float.valueOf(progress.currentBytes) / Float.valueOf(progress.totalBytes)) * 100);
                        progressBar.setFrame(146 + ((int) ((prog * 41) / 100)));
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(FullScreenActivity.this, "Download Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressBar.setMinFrame(187);
                        progressBar.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Toast.makeText(FullScreenActivity.this, "Download Completed!", Toast.LENGTH_SHORT).show();
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    registerDownload(photo.photoId);
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        progressBar.playAnimation();
                        ///////// Setting Downloads
                        setDownloads(photo.photoId);
                        ///////// Setting Downloads
                        if (isSetWallpaper) {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            CropImage.activity(Uri.fromFile(new File(currentImagePath)))
                                    .setAspectRatio(width, height)
                                    .start(FullScreenActivity.this);
                        } else if (isShare) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(FullScreenActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(currentImagePath));
                            sharingIntent.setType("*/*");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        }
                    }


                    @Override
                    public void onError(Error error) {
                        Toast.makeText(FullScreenActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setDownloads(String photoId) {
        HomeFragment.firebaseFirestore.collection("HOME_FRAGMENT").document(photoId).update("downloads", FieldValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayData();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initViews() {
        HomeFragment.firebaseFirestore.collection("HOME_FRAGMENT")
                .document(photo.photoId)
                .update("views", FieldValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

    private void registerDownload(String photoId) {
        Map<String, Object> download = new HashMap<>();
        download.put("id", photoId);
        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("downloads")
                .document(photoId)
                .set(download)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            Log.i("TAG", "requestPermission: Yep");
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 2296);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2296:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}