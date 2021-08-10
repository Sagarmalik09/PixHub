package com.example.snaphub.ProfileFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.snaphub.HomeFragment.HomeModel;
import com.example.snaphub.HomeFragment.HomePageAdapter;
import com.example.snaphub.HomeFragment.OnLikedCallback;
import com.example.snaphub.LatestFragment.LatestAdapter;
import com.example.snaphub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;

public class DownloadsActivity extends AppCompatActivity {

    private RecyclerView downloadsRecyclerView;
    private AppBarLayout appBarLayout;
    private List<HomeModel> downloadsList;

    private String tileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        tileName = getIntent().getStringExtra("tile_name");


        ///// Assignment
        downloadsRecyclerView = findViewById(R.id.downloads_recyclerView);
        downloadsList = new ArrayList<>();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        ///// Assignment

        init();
    }

    private void init() {


        //// Recycler ////
        switch (tileName) {
            case "downloads":
                getSupportActionBar().setTitle("Downloads");
                GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                downloadsRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                LatestAdapter adapter = new LatestAdapter(downloadsList);
                downloadsRecyclerView.setAdapter(adapter);
                setData("downloads", new OnLikedCallback() {
                    @Override
                    public void onLiked() {
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            case "favorites":
                getSupportActionBar().setTitle("Favorites");
                LinearLayoutManager homeLayoutManager = new LinearLayoutManager(this);
                homeLayoutManager.setOrientation(RecyclerView.VERTICAL);
                downloadsRecyclerView.setLayoutManager(homeLayoutManager);
                HomePageAdapter homeAdapter = new HomePageAdapter(this, downloadsList);
                downloadsRecyclerView.setAdapter(homeAdapter);
                setData("liked_photos", new OnLikedCallback() {
                    @Override
                    public void onLiked() {
                        homeAdapter.notifyDataSetChanged();
                    }
                });
                break;
            default:
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        //// Recycler ////

    }

    private void setData(String collectionName, OnLikedCallback callback) {

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            final int[] counter = {1};
                            int size = task.getResult().getDocuments().size();
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                FirebaseFirestore.getInstance().collection("HOME_FRAGMENT").document(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot snapshot = task.getResult();
                                            downloadsList.add(new HomeModel(
                                                    snapshot.getId(),
                                                    snapshot.get("image_4k").toString(),
                                                    snapshot.get("image_1080p").toString(),
                                                    snapshot.get("image_720p").toString(),
                                                    snapshot.get("image_480p").toString(),
                                                    snapshot.get("name").toString(),
                                                    (long) snapshot.get("color"),
                                                    (long) snapshot.get("height"),
                                                    (long) snapshot.get("width"),
                                                    (long) snapshot.get("likes"),
                                                    (long) snapshot.get("downloads"),
                                                    (long) snapshot.get("views")
                                            ));
                                            if (counter[0] == size) {
                                                callback.onLiked();
                                            }
                                            counter[0]++;
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(DownloadsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DownloadsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}