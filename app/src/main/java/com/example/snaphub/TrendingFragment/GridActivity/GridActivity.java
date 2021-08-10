package com.example.snaphub.TrendingFragment.GridActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.snaphub.HomeFragment.HomeModel;
import com.example.snaphub.LatestFragment.LatestAdapter;
import com.example.snaphub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;

public class GridActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;

    private RecyclerView gridRecyclerView;
    private List<HomeModel> filteredImagesList;

    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        ///// Getting Intent Data
        categoryName = getIntent().getStringExtra("category");
        ///// Getting Intent Data

        /////////// Assignment
        gridRecyclerView = findViewById(R.id.grid_recyclerView);

        filteredImagesList = new ArrayList<>();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(categoryName.toUpperCase());

        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        /////////// Assignment




        init();
    }

    private void init() {
        ////// RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        FirebaseFirestore.getInstance().collection("HOME_FRAGMENT").whereArrayContains("categories", categoryName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                        filteredImagesList.add(new HomeModel(
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
                    }

                    LatestAdapter adapter = new LatestAdapter(filteredImagesList);
                    gridRecyclerView.setAdapter(adapter);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(GridActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ////// RecyclerView
    }
}