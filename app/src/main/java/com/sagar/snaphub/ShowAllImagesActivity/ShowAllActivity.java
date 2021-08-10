package com.sagar.snaphub.ShowAllImagesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sagar.snaphub.HomeFragment.HomeModel;
import com.sagar.snaphub.HomeFragment.HomePageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.sagar.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;

import sagar.snaphub.R;

public class ShowAllActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private RecyclerView showAllRecyclerView;
    private Intent dataIntent;

    private ArrayList<HomeModel> homeModelArrayList;
    private String collectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        dataIntent = getIntent();
        collectionId = dataIntent.getStringExtra("id");

        ////////////// Assignment
        showAllRecyclerView = findViewById(R.id.show_all_recyclerView);

        homeModelArrayList = new ArrayList<>();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(dataIntent.getStringExtra("title").toUpperCase());

        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();

        homeModelArrayList = new ArrayList<>();
        ////////////// Assignment

        init();

    }

    private void init() {

        /////////// RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        showAllRecyclerView.setLayoutManager(layoutManager);



        FirebaseFirestore.getInstance().collection("COLLECTIONS").document(collectionId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    final ArrayList<String> imageIds = ((ArrayList<String>) snapshot.get("collection"));
                    final int[] counter = {1};
                    for (String imageId : imageIds) {
                        FirebaseFirestore.getInstance().collection("HOME_FRAGMENT").document(imageId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot imageData = task.getResult();
                                    homeModelArrayList.add(new HomeModel(
                                            imageData.getId(),
                                            imageData.get("image_4k").toString(),
                                            imageData.get("image_1080p").toString(),
                                            imageData.get("image_720p").toString(),
                                            imageData.get("image_480p").toString(),
                                            imageData.get("name").toString(),
                                            (long) imageData.get("color"),
                                            (long) imageData.get("height"),
                                            (long) imageData.get("width"),
                                            (long) imageData.get("likes"),
                                            (long) imageData.get("downloads"),
                                            (long) imageData.get("views")
                                    ));

                                    if (counter[0] == imageIds.size()){
                                        HomePageAdapter homePageAdapter = new HomePageAdapter(getApplicationContext(), homeModelArrayList);
                                        showAllRecyclerView.setAdapter(homePageAdapter);
                                    }
                                    counter[0]++;
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(ShowAllActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ShowAllActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /////////// RecyclerView


    }
}