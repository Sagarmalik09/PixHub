package com.sagar.snaphub.TrendingFragment.Collection;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sagar.snaphub.PushDown.PushDownAnim;
import com.sagar.snaphub.ShowAllImagesActivity.ShowAllActivity;

import java.util.List;

import sagar.snaphub.R;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    List<CollectionModel> collectionModelList;

    public CollectionAdapter(List<CollectionModel> collectionModelList) {
        this.collectionModelList = collectionModelList;
    }

    @NonNull
    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionAdapter.ViewHolder holder, int position) {
        holder.setData(collectionModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return collectionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image1, image2, image3, image4;
        private TextView titleTextView, titleCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.img1);
            image2 = itemView.findViewById(R.id.img2);
            image3 = itemView.findViewById(R.id.img3);
            image4 = itemView.findViewById(R.id.img4);
            titleTextView = itemView.findViewById(R.id.grid_title);
            titleCountTextView = itemView.findViewById(R.id.grid_title_count);
        }

        private void setData(final CollectionModel model) {
            titleTextView.setText(model.collectionTitle);
            if (model.totalCount > 0) {
                titleCountTextView.setText(String.valueOf(model.totalCount) + " Photos");
            }

            Glide.with(itemView.getContext())
                    .load(model.imageUrlList_upTo_4.get(0))
                    .into(image1);
            Glide.with(itemView.getContext())
                    .load(model.imageUrlList_upTo_4.get(1))
                    .into(image2);
            Glide.with(itemView.getContext())
                    .load(model.imageUrlList_upTo_4.get(2))
                    .into(image3);
            Glide.with(itemView.getContext())
                    .load(model.imageUrlList_upTo_4.get(3))
                    .into(image4);

            PushDownAnim.setPushDownAnimTo(itemView)
                    .setScale(0.95f)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!model.collectionId.equals("")) {
                                Intent showAllActivityIntent = new Intent(itemView.getContext(), ShowAllActivity.class);
                                showAllActivityIntent.putExtra("title", model.collectionTitle);
                                showAllActivityIntent.putExtra("id", model.collectionId);
                                itemView.getContext().startActivity(showAllActivityIntent);
                            }
                        }
                    });
        }
    }
}
