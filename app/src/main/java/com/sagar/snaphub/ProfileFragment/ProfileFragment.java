package com.sagar.snaphub.ProfileFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.sagar.snaphub.EditProfile.EditProfileActivity;
import com.sagar.snaphub.MainActivity;
import com.sagar.snaphub.ProfileFragment.NotificationsActivity.NotificationsActivity;
import com.sagar.snaphub.PushDown.PushDownAnim;
import com.sagar.snaphub.RegisterFragment.RegisterFragment;
import com.sagar.snaphub.TrendingFragment.TopRated.TopAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sagar.snaphub.R;

import static androidx.core.app.FrameMetricsAggregator.DELAY_DURATION;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ProfileFragment extends Fragment {
    private ImageView Profileimage, backImageView, editProfileBtn;
    private TextView nameTextView, mailTextView, downloadsTextView, favoritesCountTextView;
    private Button signOutBtn;
    private GridLayout tileContainer;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ScrollView mainLayout;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private FirebaseUser currentUser;


    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ///// Assignment //////
        backImageView = view.findViewById(R.id.back_imageView);
        Profileimage = view.findViewById(R.id.profile_imageView);
        nameTextView = view.findViewById(R.id.name_textView);
        mailTextView = view.findViewById(R.id.mail_textView);
        signOutBtn = view.findViewById(R.id.sign_out_btn);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        tileContainer = view.findViewById(R.id.tiles_container);
        downloadsTextView = view.findViewById(R.id.download_textView);
        favoritesCountTextView = view.findViewById(R.id.favorites_count_textView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        mainLayout = view.findViewById(R.id.main_layout);
        ///// Assignment //////

        init();

        return view;
    }

    private void init() {
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                getActivity().getSupportFragmentManager().popBackStack();
                MainActivity.setFragment(new RegisterFragment(), "PROFILE_FRAGMENT", getActivity().getSupportFragmentManager());
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                getContext().startActivity(editProfileIntent);
            }
        });

        ///// Tiles Container
        for (int i = 0; i < tileContainer.getChildCount(); i++) {

            ImageView icon = tileContainer.getChildAt(i).findViewById(R.id.imageView);
            TextView lightTextView = tileContainer.getChildAt(i).findViewById(R.id.light_textView);
            TextView mainTextView = tileContainer.getChildAt(i).findViewById(R.id.main_textView);

            switch (i) {
                case 0:  // Downloads
                    icon.setImageDrawable(getContext().getDrawable(R.drawable.download_icon));
                    lightTextView.setText("Downloads");
                    mainTextView.setText("Downloads");
                    PushDownAnim.setPushDownAnimTo(tileContainer.getChildAt(i)).setScale(0.97f).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent downloadsActivity = new Intent(getContext(), DownloadsActivity.class);
                            downloadsActivity.putExtra("tile_name", "downloads");
                            getContext().startActivity(downloadsActivity);
                        }
                    });
                    break;
                case 1: // Favorites
                    icon.setImageDrawable(getContext().getDrawable(R.drawable.heart_thick));
                    lightTextView.setText("Favorites");
                    mainTextView.setText("Favorites");
                    PushDownAnim.setPushDownAnimTo(tileContainer.getChildAt(i)).setScale(0.97f).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent downloadsActivity = new Intent(getContext(), DownloadsActivity.class);
                            downloadsActivity.putExtra("tile_name", "favorites");
                            getContext().startActivity(downloadsActivity);
                        }
                    });
                    break;
                case 2: // Notification
                    icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_notifications_black_24dp));
                    lightTextView.setText("Notification");
                    mainTextView.setText("Notification");
                    PushDownAnim.setPushDownAnimTo(tileContainer.getChildAt(i)).setScale(0.97f).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent notificationsActivity = new Intent(getContext(), NotificationsActivity.class);
                            getContext().startActivity(notificationsActivity);
                        }
                    });
                    break;
                case 3: // Rate
                    icon.setImageDrawable(getContext().getDrawable(R.drawable.three_stars));
                    lightTextView.setText("Rate");
                    mainTextView.setText("Rate");
                    PushDownAnim.setPushDownAnimTo(tileContainer.getChildAt(i)).setScale(0.97f).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseFirestore
                                    .collection("APP_LINK")
                                    .document("play_store_link")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String appLink = task.getResult().get("link").toString();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appLink));
                                                startActivity(browserIntent);
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    break;
            }

        }
        ///// Tiles Container

        /// Likes Count
        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("liked_photos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            favoritesCountTextView.setText(TopAdapter.format(task.getResult().getDocuments().size()));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /// Likes Count

        /// Downloads Count
        FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("downloads")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            downloadsTextView.setText(TopAdapter.format(task.getResult().getDocuments().size()));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /// Downloads Count

    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        googleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
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
                        String imageUrl = snapshot.get("user_profile_pic").toString();
                        if (!imageUrl.equals("")) {
                            if (!((Activity) getContext()).isFinishing()) {
                                Glide.with(getContext())
                                        .load(imageUrl)
                                        .centerCrop()
                                        .circleCrop()
                                        .into(Profileimage);
                            }
                            setBlurImageToBackground(getContext(), imageUrl, backImageView);
                        } else {
                            Glide.with(getContext())
                                    .load(R.drawable.ic_avatar)
                                    .centerCrop()
                                    .circleCrop()
                                    .into(Profileimage);
                        }
                        nameTextView.setText(snapshot.get("first_name").toString() + " " + snapshot.get("last_name").toString());
                        mailTextView.setText(snapshot.get("email").toString());
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void setBlurImageToBackground(final Context context, final String path, final ImageView imageView) {
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(context).load(path)
                        .apply(bitmapTransform(new BlurTransformation(35)))
                        .into(imageView);
            }
        }, DELAY_DURATION);
    }
}