package com.example.snaphub.HomeFragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snaphub.MainActivity;
import com.example.snaphub.ProfileFragment.ProfileFragment;
import com.example.snaphub.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseUser currentUser;
    public static Resources resources;

    public HomeFragment() {
    }

    private RecyclerView homeRecyclerview;
    public static HomePageAdapter adapter;
    private ImageView profilePicImageView;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ShimmerFrameLayout shimmerFrameLayout;

    public static DocumentSnapshot lastVisibleItem;
    public static boolean isScrolling = false;
    public static boolean isLastItemReached = false;
    public static boolean isLoading = false;


    List<HomeModel> homePageModelList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        resources = getContext().getResources();

        //// assignment
        homeRecyclerview = view.findViewById(R.id.home_recyclerView);
        profilePicImageView = view.findViewById(R.id.main_profile_pic_imageView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);

        appBarLayout = view.findViewById(R.id.appbar_layout);
        toolbar = view.findViewById(R.id.toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        //// assignment

        initToolbar();
        init();

        return view;
    }

    private void init() {

        ///// RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        homeRecyclerview.setLayoutManager(layoutManager);
        ///// RecyclerView

        ///// Fetching Data
        firebaseFirestore.collection("HOME_FRAGMENT")
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> imageDataList = task.getResult().getDocuments();
                            for (DocumentSnapshot snapshot : imageDataList) {
                                homePageModelList.add(new HomeModel(
                                        snapshot.getId(),
                                        snapshot.get("image_4k").toString(),
                                        snapshot.get("image_1080p").toString(),
                                        snapshot.get("image_480p").toString(),
                                        snapshot.get("image_720p").toString(),
                                        snapshot.get("name").toString(),
                                        (long) snapshot.get("color"),
                                        (long) snapshot.get("height"),
                                        (long) snapshot.get("width"),
                                        (long) snapshot.get("likes"),
                                        (long) snapshot.get("downloads"),
                                        (long) snapshot.get("views")
                                ));

                                if (homePageModelList.size() == task.getResult().size()) {
                                    adapter = new HomePageAdapter(getContext(), homePageModelList);
                                    homeRecyclerview.setAdapter(adapter);

                                    lastVisibleItem = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                isScrolling = true;
                                            }
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);

                                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                            int visibleItemCount = linearLayoutManager.getChildCount();
                                            int totalItemCount = linearLayoutManager.getItemCount();
                                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                                isScrolling = false;

                                                if (!isLoading) {
                                                    isLoading = true;
                                                    homePageModelList.add(null);
                                                    recyclerView.getAdapter().notifyItemInserted(homePageModelList.size() - 1);
                                                    int tempPos = homePageModelList.size() - 1;

                                                    Query newQuery = firebaseFirestore.collection("HOME_FRAGMENT")
                                                            .startAfter(lastVisibleItem)
                                                            .limit(15);
                                                    newQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (DocumentSnapshot newSnapshot : task.getResult().getDocuments()) {
                                                                    homePageModelList.add(new HomeModel(
                                                                            newSnapshot.getId(),
                                                                            newSnapshot.get("image_4k").toString(),
                                                                            newSnapshot.get("image_1080p").toString(),
                                                                            newSnapshot.get("image_480p").toString(),
                                                                            newSnapshot.get("image_720p").toString(),
                                                                            newSnapshot.get("name").toString(),
                                                                            (long) newSnapshot.get("color"),
                                                                            (long) newSnapshot.get("height"),
                                                                            (long) newSnapshot.get("width"),
                                                                            (long) newSnapshot.get("likes"),
                                                                            (long) newSnapshot.get("downloads"),
                                                                            (long) newSnapshot.get("views")
                                                                    ));
                                                                    recyclerView.getAdapter().notifyItemInserted(homePageModelList.size() - 1);
                                                                }
                                                                homePageModelList.remove(tempPos);
                                                                recyclerView.getAdapter().notifyItemRemoved(tempPos);

                                                                if ((task.getResult().size() - 1) != -1){
                                                                    lastVisibleItem = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                }
                                                                isLoading = false;
                                                                if (task.getResult().size() < 15){
                                                                    isLastItemReached = true;
                                                                }
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
                                    homeRecyclerview.addOnScrollListener(onScrollListener);

                                }

                            }


                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            homeRecyclerview.setVisibility(View.VISIBLE);

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ///// Fetching Data

    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            firebaseFirestore.collection("USERS").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        String profilePicUrl = snapshot.get("user_profile_pic").toString();
                        if (!profilePicUrl.equals("")) {
                            Glide.with(getContext())
                                    .load(profilePicUrl)
                                    .centerCrop()
                                    .circleCrop()
                                    .into(profilePicImageView);
                        }
                        profilePicImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.setFragment(new ProfileFragment(), "PROFILE_FRAGMENT", getParentFragmentManager());
                                MainActivity.navigation.getMenu().findItem(R.id.navigation_profile).setChecked(true);
                            }
                        });
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void initToolbar() {
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        profilePicImageView.setPadding(32, getStatusBarHeight(), 32, 0);
        profilePicImageView.getLayoutParams().height += getStatusBarHeight();
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}