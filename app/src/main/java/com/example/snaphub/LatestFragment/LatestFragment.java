package com.example.snaphub.LatestFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.snaphub.HomeFragment.HomeModel;
import com.example.snaphub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;


public class LatestFragment extends Fragment {
    private RecyclerView latestRv;
    private List<HomeModel> latestModelList;
    private ProgressBar progressBar;

    private Toolbar toolbar;


    public LatestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_latest, container, false);


        ///assignment///
        latestRv = view.findViewById(R.id.latest_imageView);
        toolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progressBar);

        latestModelList = new ArrayList<>();
        ///assignment///

        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();

        init();

        return view;

    }

    private void init() {
        ///////////////// RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        latestRv.setLayoutManager(staggeredGridLayoutManager);
        FirebaseFirestore.getInstance()
                .collection("HOME_FRAGMENT")
                .whereArrayContains("categories", "sports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                latestModelList.add(new HomeModel(
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

                            LatestAdapter adapter = new LatestAdapter(latestModelList);
                            latestRv.setAdapter(adapter);

                            progressBar.setVisibility(View.GONE);
                            latestRv.setVisibility(View.VISIBLE);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ///////////////// RecyclerView

    }
}