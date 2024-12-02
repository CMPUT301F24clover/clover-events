package com.example.luckyevent.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.luckyevent.R;
import com.example.luckyevent.activities.BrowseImagesActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private final Context context;
    private final ArrayList<HashMap<String, String>> imageList;

    public ImageListAdapter(Context context, ArrayList<HashMap<String, String>> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        HashMap<String, String> imageInfo = imageList.get(position);
        String imageUrl = imageInfo.get("url");
        String description = imageInfo.get("description");

        // Log the image URL for debugging
        Log.d("ImageListAdapter", "Image URL: " + imageUrl);

        // Load the image with Glide, including placeholder and error handling
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.demo_profile)  // Add a placeholder image in drawable
                .error(R.drawable.demo_profile)              // Add an error image in drawable
                .into(holder.imageView);

        holder.descriptionText.setText(description);

        holder.removeButton.setOnClickListener(v -> {
            String id = imageInfo.get("id");
            String collection = imageInfo.get("collection");
            if (context instanceof BrowseImagesActivity) {
                ((BrowseImagesActivity) context).removeImage(id, collection, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionText;
        Button removeButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
