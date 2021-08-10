package com.sagar.snaphub.TrendingFragment.TopRated;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sagar.snaphub.FullScreenActivity;
import com.sagar.snaphub.HomeFragment.HomeFragment;
import com.sagar.snaphub.HomeFragment.HomeModel;
import com.sagar.snaphub.HomeFragment.HomePageAdapter;
import com.sagar.snaphub.HomeFragment.OnLikedCallback;
import com.sagar.snaphub.PushDown.PushDownAnim;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import sagar.snaphub.R;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder> {

    ArrayList<HomeModel> imageList;

    public TopAdapter(ArrayList<HomeModel> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public TopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_rated_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAdapter.ViewHolder holder, int position) {
        holder.setData(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView, likeBtn;
        private TextView likesCount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ///assignment//
            imageView = itemView.findViewById(R.id.back_imageView);
            likesCount = itemView.findViewById(R.id.likes_count_textView);
            likeBtn = itemView.findViewById(R.id.like_btn);
            ///assignment//
        }

        private void setData(final HomeModel model) {
            Glide.with(itemView.getContext())
                    .load(model.image_480p)
                    .transform(new CenterCrop(), new RoundedCorners(50))
                    .into(imageView);

            likesCount.setText(format(model.likes));

            if (model.height != 0) {
                if (HomeFragment.currentUser != null) {
                    HomeFragment.firebaseFirestore.collection("USERS")
                            .document(HomeFragment.currentUser.getUid())
                            .collection("liked_photos")
                            .document(model.photoId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            likeBtn.setImageDrawable(itemView.getContext().getDrawable(R.drawable.heart_active));
                                            likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFF0000")));
                                            model.isLiked = true;
                                        } else {
                                            model.isLiked = false;
                                        }
                                        likeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                HomePageAdapter.toggleLike(itemView.getContext(), model, likeBtn, true, new OnLikedCallback() {
                                                    @Override
                                                    public void onLiked() {
                                                        displayData(model, likesCount);
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            PushDownAnim.setPushDownAnimTo(itemView)
                    .setScale(0.95f)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (model.height != 0) {
                                Intent fullScreenIntent = new Intent(itemView.getContext(), FullScreenActivity.class);
                                fullScreenIntent.putExtra("image_id", model);
                                fullScreenIntent.putExtra("position", getAdapterPosition());
                                fullScreenIntent.putExtra("adapterName", "TOP_RATED");
                                itemView.getContext().startActivity(fullScreenIntent);
                            }
                        }
                    });
        }

        private void displayData(HomeModel model, final TextView likesCountTextView) {
            HomeFragment.firebaseFirestore.collection("HOME_FRAGMENT")
                    .document(model.photoId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                likesCountTextView.setText(format((long) snapshot.get("likes")));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private static final char[] SUFFIXES = {'k', 'm', 'g', 't', 'p', 'e'};

    public static String format(long number) {
        if (number < 1000) {
            // No need to format this
            return String.valueOf(number);
        }
        // Convert to a string
        final String string = String.valueOf(number);
        // The suffix we're using, 1-based
        final int magnitude = (string.length() - 1) / 3;
        // The number of digits we must show before the prefix
        final int digits = (string.length() - 1) % 3 + 1;

        // Build the string
        char[] value = new char[4];
        for (int i = 0; i < digits; i++) {
            value[i] = string.charAt(i);
        }
        int valueLength = digits;
        // Can and should we add a decimal point and an additional number?
        if (digits == 1 && string.charAt(1) != '0') {
            value[valueLength++] = '.';
            value[valueLength++] = string.charAt(1);
        }
        value[valueLength++] = SUFFIXES[magnitude - 1];
        return new String(value, 0, valueLength);
    }
}
