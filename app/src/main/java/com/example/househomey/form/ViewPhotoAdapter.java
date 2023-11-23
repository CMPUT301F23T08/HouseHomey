package com.example.househomey.form;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.househomey.MainActivity;
import com.example.househomey.R;

import java.util.List;

/**
 * Custom adapter for displaying photos to a RecyclerView.
 */
public class ViewPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<String> imageUris;

    /**
     * Constructs a ViewPhotoAdapter with the given context and a list of image URIs to display.
     *
     * @param context   The context.
     * @param imageUris The list of image URIs.
     */
    public ViewPhotoAdapter(Context context, List<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    /**
     * Creates new view holders for the RecyclerView based on their view type.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gallery_photo, parent, false);
        return new ViewImageViewHolder(view);
    }

    /**
     * Binds the click handler for the add button.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewPhotoAdapter.ViewImageViewHolder) {
            loadIntoImageView(((ViewImageViewHolder) holder).imageView, imageUris.get(position));
        }
    }

    /**
     * Loads the image defined by a URI into an ImageView using Glide.
     *
     * @param imageView The ImageView to load the image into.
     * @param imagePath The string URI of the image.
     */
    private void loadIntoImageView(ImageView imageView, String imagePath) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).asDrawable();
        if (imagePath.contains("content://") || imagePath.contains("file://")) {
            // Local file URI, load directly
            requestBuilder.load(imagePath);
        } else {
            // Cloud Storage URI, fetch from Firebase
            requestBuilder.load(((MainActivity) context).getImageRef(imagePath));
        }
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
    }

    /**
     * Getter for the number of items held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder for displaying images in the RecyclerView.
     */
    public static class ViewImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructs an ImageViewHolder using the given itemView.
         *
         * @param itemView The view for this ViewHolder.
         */
        public ViewImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
        }
    }
}

