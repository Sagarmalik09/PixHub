package com.sagar.snaphub.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sagar.snaphub.FullScreenActivity;
import com.sagar.snaphub.TrendingFragment.TopRated.TopAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sagar.snaphub.R;

public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<HomeModel> imageList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public HomePageAdapter(Context context, List<HomeModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getItemViewType(int position) {
        return imageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());

        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            HomeModel currentItem = imageList.get(position);
            ((ViewHolder) holder).setData(currentItem);
        } else {
            // TODO : Handle Loading Layout
        }
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView mainImageView;
        private TextView maintv, likesCountTextView;
        private ImageView miniImageView;
        private ImageView likeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /// Assignment
            mainImageView = itemView.findViewById(R.id.placeholder_imagevIew);
            maintv = itemView.findViewById(R.id.top_Tv);
            miniImageView = itemView.findViewById(R.id.small_image);
            likeBtn = itemView.findViewById(R.id.small_like);
            likesCountTextView = itemView.findViewById(R.id.likes_count_textView);
            /// Assignment
        }

        private void setData(final HomeModel currentItem) {

            maintv.setText(currentItem.name);

            likesCountTextView.setText(TopAdapter.format(currentItem.likes));

            /////// Setting Image ////////

            final ControllerListener listener = new BaseControllerListener<ImageInfo>() {

                @Override
                public void onSubmit(String id, Object callerContext) {
                    if (currentItem.width != 0 || currentItem.height != 0) {
                        try {
                            mainImageView.setBackgroundColor((int) currentItem.color);
                        } catch (Exception e) {
                        }
                        mainImageView.setAspectRatio((float) currentItem.width / currentItem.height);
                    }
                }

                @Override
                public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {

                }
            };

            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(currentItem.image_480p))
                    .setRequestPriority(Priority.HIGH)
                    .setProgressiveRenderingEnabled(true)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setControllerListener(listener)
                    .setCallerContext(mainImageView.getContext())
                    .setImageRequest(request)
                    .setOldController(mainImageView.getController())
                    .build();

            mainImageView.setController(controller);

            Glide.with(itemView.getContext())
                    .load(currentItem.image_480p)
                    .centerCrop().circleCrop()
                    .into(miniImageView);
            /////// Setting Image ////////

            if (HomeFragment.currentUser != null) {
                HomeFragment.firebaseFirestore.collection("USERS")
                        .document(HomeFragment.currentUser.getUid())
                        .collection("liked_photos")
                        .document(currentItem.photoId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        likeBtn.setImageDrawable(context.getDrawable(R.drawable.heart_active));
                                        likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFF0000")));
                                        currentItem.isLiked = true;
                                    } else {
                                        currentItem.isLiked = false;
                                    }
                                    likeBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            toggleLike(itemView.getContext(), currentItem, likeBtn, false, new OnLikedCallback() {
                                                @Override
                                                public void onLiked() {
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullScreenIntent = new Intent(itemView.getContext(), FullScreenActivity.class);
                    fullScreenIntent.putExtra("image_id", currentItem);
                    fullScreenIntent.putExtra("adapterName", "HOME_FRAGMENT");
                    fullScreenIntent.putExtra("position", getAdapterPosition());
                    itemView.getContext().startActivity(fullScreenIntent);
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static void toggleLike(final Context context, final HomeModel currentItem, final ImageView likeBtn, final boolean isTopRated, final OnLikedCallback callback) {
        if (HomeFragment.currentUser != null) {
            DocumentReference userLikedPhotoSnapshot = HomeFragment.firebaseFirestore.collection("USERS")
                    .document(HomeFragment.currentUser.getUid())
                    .collection("liked_photos")
                    .document(currentItem.photoId);
            final DocumentReference photoSnapshot = HomeFragment.firebaseFirestore.collection("HOME_FRAGMENT")
                    .document(currentItem.photoId);
            if (currentItem.isLiked) {
                ////// Unlike Photo
                userLikedPhotoSnapshot.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            photoSnapshot.update("likes", FieldValue.increment(-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (!isTopRated) {
                                            likeBtn.setImageDrawable(context.getDrawable(R.drawable.heart_thick));
                                            likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                                        } else {
                                            likeBtn.setImageDrawable(context.getDrawable(R.drawable.heart_thick));
                                            likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                        }
                                        currentItem.isLiked = false;
                                        callback.onLiked();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ////// Like Photo
                Map<String, Object> data = new HashMap<>();
                data.put("id", currentItem.photoId);
                userLikedPhotoSnapshot.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            photoSnapshot.update("likes", FieldValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        likeBtn.setImageDrawable(context.getDrawable(R.drawable.heart_active));
                                        likeBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFF0000")));
                                        currentItem.isLiked = true;
                                        callback.onLiked();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
