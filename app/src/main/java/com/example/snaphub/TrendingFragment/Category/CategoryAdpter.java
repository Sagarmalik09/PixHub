package com.example.snaphub.TrendingFragment.Category;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.snaphub.PushDown.PushDownAnim;
import com.example.snaphub.R;
import com.example.snaphub.TrendingFragment.GridActivity.GridActivity;

import java.util.ArrayList;

public class CategoryAdpter extends RecyclerView.Adapter<CategoryAdpter.ViewHolder> {

    ArrayList<CategoryModel> imageList;

    public CategoryAdpter(ArrayList<CategoryModel> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public CategoryAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdpter.ViewHolder holder, int position) {
        holder.setData(imageList.get(position));
    }

    @Override
    public int getItemCount() {

        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView TrendingimageView;
        private TextView TrendingtextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //assignment///
            TrendingimageView = itemView.findViewById(R.id.TRENDING_IMAGE);
            TrendingtextView = itemView.findViewById(R.id.TRENDING_TEXT);
            //assignment///
        }

        private void setData(final CategoryModel model) {
            Glide.with(itemView.getContext())
                    .load(model.imageUrl)
                    .transform(new CenterCrop(), new RoundedCorners(50))
                    .into(TrendingimageView);
            TrendingtextView.setText(model.categoryName);


            PushDownAnim.setPushDownAnimTo(itemView)
                    .setScale(0.95f)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!model.categoryName.equals("")) {
                                Intent gridActivity = new Intent(itemView.getContext(), GridActivity.class);
                                gridActivity.putExtra("category", model.categoryName);
                                itemView.getContext().startActivity(gridActivity);
                            }
                        }
                    });
        }

    }
}
