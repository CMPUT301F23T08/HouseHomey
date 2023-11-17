package com.example.househomey.form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.househomey.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUris;

    public PhotoAdapter(Context context, List<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageUris.get(position);
        if (imagePath.contains("content")) {
            // Local file URI, use directly
            Glide.with(context)
                    .load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.imageView);
        } else {
            // Cloud Storage URI, create a Firebase reference and fetch
            imagePath = "images/" + imageUris.get(position);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(imagePath);
            Glide.with(context)
                    .load(storageReference)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
        }
    }
}
