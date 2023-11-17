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

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 1;
    private final Context context;
    private final List<String> imageUris;
    private OnAddButtonClickListener onAddButtonClickListener;

    public PhotoAdapter(Context context, List<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
        this.onAddButtonClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.gallery_photo, parent, false);
            return new ImageViewHolder(view);
        }
        View view = inflater.inflate(R.layout.add_photo_button, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            ImageView imageView = ((ImageViewHolder) holder).imageView;
            String imagePath = imageUris.get(position);
            if (imagePath.contains("content://") || imagePath.contains("file://")) {
                // Local file URI, so just load directly
                Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
            } else {
                // Cloud Storage URI, so create a Firebase reference and fetch
                imagePath = "images/" + imageUris.get(position);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(imagePath);
                Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
            }
        }
        else {
            holder.itemView.setOnClickListener(v -> onAddButtonClickListener.onAddButtonClicked());
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 to account for Add button always at the end
        return imageUris.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == imageUris.size()) ? 0 : VIEW_TYPE_IMAGE;
    }

    public interface OnAddButtonClickListener {
        void onAddButtonClicked();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
        }
    }
}
