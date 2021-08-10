package com.example.snaphub.TrendingFragment;

import static com.example.snaphub.HomeFragment.HomeFragment.getStatusBarHeight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snaphub.HomeFragment.HomeModel;
import com.example.snaphub.R;
import com.example.snaphub.TrendingFragment.Category.CategoryAdpter;
import com.example.snaphub.TrendingFragment.Category.CategoryModel;
import com.example.snaphub.TrendingFragment.Collection.CollectionAdapter;
import com.example.snaphub.TrendingFragment.Collection.CollectionModel;
import com.example.snaphub.TrendingFragment.TopRated.TopAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {

    public static TopAdapter topAdapter;

    private LinearLayout parentLayout;

    private RecyclerView collectionRV, categoriesRV, topRatedRV;

    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    ArrayList<HomeModel> topRatedList = new ArrayList<>();
    ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<>();

    ArrayList<CategoryModel> dummyCategoryModelList = new ArrayList<>();
    ArrayList<HomeModel> dummyTopRatedList = new ArrayList<>();
    ArrayList<CollectionModel> dummyCollectionModelArrayList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ///assignment///
        collectionRV = view.findViewById(R.id.collection_rv);
        categoriesRV = view.findViewById(R.id.categories_rv);
        topRatedRV = view.findViewById(R.id.top_Rated_rv);

        parentLayout = view.findViewById(R.id.trendingParentLayout);
        ///assignment///

        parentLayout.setPadding(0, getStatusBarHeight(), 0, 0);
        parentLayout.getLayoutParams().height += getStatusBarHeight();

        showDummyData();
        init();

        return view;


    }

    private void init() {
        ///// Categories RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoriesRV.setLayoutManager(layoutManager);
        FirebaseFirestore.getInstance().collection("CATEGORIES").orderBy("index", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        categoryModelList.add(new CategoryModel(
                                snapshot.get("image_url").toString(),
                                snapshot.get("title").toString()
                        ));
                    }
                    CategoryAdpter adapter = new CategoryAdpter(categoryModelList);
                    categoriesRV.setAdapter(adapter);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ///// Categories RecyclerView


        ///// Collection RecyclerView
        LinearLayoutManager collectionLayoutManager = new LinearLayoutManager(getContext());
        collectionLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        collectionRV.setLayoutManager(collectionLayoutManager);

        FirebaseFirestore.getInstance().collection("COLLECTIONS").orderBy("release_date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final int[] counter = {0};
                    final int totalSize = task.getResult().getDocuments().size() - 1;
                    for (final DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        final ArrayList<String> imageList = new ArrayList<>();
                        final int[] imageCounter = {1};
                        for (int i = 0; i < 4; i++) {
                            FirebaseFirestore.getInstance().collection("HOME_FRAGMENT")
                                    .document(((ArrayList<String>) snapshot.get("collection")).get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot imageData = task.getResult();
                                                imageList.add(imageData.get("image_480p").toString());

                                                if (imageCounter[0] == 4) {
                                                    collectionModelArrayList.add(new CollectionModel(
                                                            snapshot.getId(),
                                                            snapshot.get("title").toString(),
                                                            ((ArrayList<String>) snapshot.get("collection")).size(),
                                                            imageList
                                                    ));

                                                    if (counter[0] == totalSize) {
                                                        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionModelArrayList);
                                                        collectionRV.setAdapter(collectionAdapter);
                                                    }
                                                    counter[0]++;
                                                }
                                                imageCounter[0]++;
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///// Collection RecyclerView


        ///// Top Rated RecyclerView
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        topRatedRV.setLayoutManager(layoutManager2);
        FirebaseFirestore.getInstance().collection("HOME_FRAGMENT").orderBy("likes", Query.Direction.DESCENDING).limit(15).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int counter = 0;

                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        topRatedList.add(new HomeModel(
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

                        if (counter == task.getResult().getDocuments().size() - 1) {
                            topAdapter = new TopAdapter(topRatedList);
                            topRatedRV.setAdapter(topAdapter);
                        }
                        counter++;
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///// Top Rated RecyclerView
    }

    private void showDummyData() {
        /////// Dummy Categories //////
        dummyCategoryModelList.add(new CategoryModel("", ""));
        dummyCategoryModelList.add(new CategoryModel("", ""));
        dummyCategoryModelList.add(new CategoryModel("", ""));
        dummyCategoryModelList.add(new CategoryModel("", ""));
        dummyCategoryModelList.add(new CategoryModel("", ""));

        LinearLayoutManager dummyLayoutManager = new LinearLayoutManager(getContext());
        dummyLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoriesRV.setLayoutManager(dummyLayoutManager);
        CategoryAdpter dummyCategoryAdapter = new CategoryAdpter(dummyCategoryModelList);
        categoriesRV.setAdapter(dummyCategoryAdapter);
        /////// Dummy Categories //////

        /////// Dummy Collection //////
        ArrayList<String> dumyImageList = new ArrayList<>();
        dumyImageList.add("");
        dumyImageList.add("");
        dumyImageList.add("");
        dumyImageList.add("");
        dummyCollectionModelArrayList.add(new CollectionModel("", "", 0, dumyImageList));
        dummyCollectionModelArrayList.add(new CollectionModel("", "", 0, dumyImageList));
        dummyCollectionModelArrayList.add(new CollectionModel("", "", 0, dumyImageList));
        dummyCollectionModelArrayList.add(new CollectionModel("", "", 0, dumyImageList));
        LinearLayoutManager dummyCollectionLayoutManager = new LinearLayoutManager(getContext());
        dummyCollectionLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        collectionRV.setLayoutManager(dummyCollectionLayoutManager);
        CollectionAdapter dummyCollectionAdapter = new CollectionAdapter(dummyCollectionModelArrayList);
        collectionRV.setAdapter(dummyCollectionAdapter);
        /////// Dummy Collection //////

        /////// Dummy Top Rated //////
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        dummyTopRatedList.add(new HomeModel("", "", "", "", "", "", 0, 0, 0, 0, 0, 0));
        LinearLayoutManager dummyTopRatedLayoutManager = new LinearLayoutManager(getContext());
        dummyTopRatedLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        topRatedRV.setLayoutManager(dummyTopRatedLayoutManager);
        TopAdapter dummyTopAdapter = new TopAdapter(dummyTopRatedList);
        topRatedRV.setAdapter(dummyTopAdapter);
        /////// Dummy Top Rated //////
    }
}