package com.example.snaphub.LatestFragment;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snaphub.FullScreenActivity;
import com.example.snaphub.HomeFragment.HomeFragment;
import com.example.snaphub.HomeFragment.HomeModel;
import com.example.snaphub.PushDown.PushDownAnim;
import com.example.snaphub.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


import java.util.List;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ViewHolder> {

    private List<HomeModel> latestModelList;

    public LatestAdapter(List<HomeModel> latestModelList) {
        this.latestModelList = latestModelList;
    }

    @NonNull
    @Override
    public LatestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestAdapter.ViewHolder holder, int position) {
        HomeModel currentModel = latestModelList.get(position);
        holder.setData(currentModel);
    }

    @Override
    public int getItemCount() {
        return latestModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.latest_imageView);
        }

        private void setData(final HomeModel currentItem) {

            /////// Setting Image ////////

            final ControllerListener listener = new BaseControllerListener<ImageInfo>() {

                @Override
                public void onSubmit(String id, Object callerContext) {
                    if (currentItem.width != 0 || currentItem.height != 0) {
                        try {
                            imageView.setBackgroundColor((int) currentItem.color);
                        } catch (Exception e) {
                        }
                        imageView.setAspectRatio((float) currentItem.width / currentItem.height);
                    }
//                imageViewHolder.mShimmerViewContainer.startShimmer();
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
                    .setCallerContext(imageView.getContext())
                    .setImageRequest(request)
                    .setOldController(imageView.getController())
                    .build();

            imageView.setController(controller);


            /////// Setting Image ////////


            PushDownAnim.setPushDownAnimTo(itemView)
                    .setScale(0.95f)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent fullScreenIntent = new Intent(itemView.getContext(), FullScreenActivity.class);
                            fullScreenIntent.putExtra("image_id", currentItem);
                            fullScreenIntent.putExtra("position", getAdapterPosition());
                            itemView.getContext().startActivity(fullScreenIntent);
                        }
                    });
        }

    }
}
