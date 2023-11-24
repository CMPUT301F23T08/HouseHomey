package com.example.househomey.form;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
 * Custom adapter for displaying and adding photos to a RecyclerView.
 *
 * @author Owen Cooke
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_IMAGE = 1;
    private final Context context;
    private final List<String> imageUris;
    private final OnAddButtonClickListener onAddButtonClickListener;

    /**
     * Constructs a PhotoAdapter with the given context and a list of image URIs to display.
     *
     * @param context   The context.
     * @param imageUris The list of image URIs.
     */
    public PhotoAdapter(Context context, List<String> imageUris, OnAddButtonClickListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.onAddButtonClickListener = listener;
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
        if (viewType == VIEW_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.gallery_photo, parent, false);
            return new ImageViewHolder(view);
        }
        View view = inflater.inflate(R.layout.add_photo_button, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Required for loading the URI at position into the actual ImageView.
     * Also binds the click handler for the add button.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            loadIntoImageView(((ImageViewHolder) holder).imageView, imageUris.get(position));
        } else {
            holder.itemView.setOnClickListener(v -> onAddButtonClickListener.onAddButtonClicked());
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
     * Add 1 to the total to account for the Add Button, which is always at the end of the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return imageUris.size() + 1;
    }

    /**
     * Return the view type of the item at position for the purposes of view recycling.
     * All Views handled as image types, except the last element, which is the add button.
     *
     * @param position – position to query
     * @return integer value identifying the type of the view needed to represent the item at position.
     */
    @Override
    public int getItemViewType(int position) {
        return (position == imageUris.size()) ? 0 : VIEW_TYPE_IMAGE;
    }

    /**
     * Interface for a callback when the add button is clicked.
     */
    public interface OnAddButtonClickListener {
        void onAddButtonClicked();
    }

    /**
     * ViewHolder for displaying images in the RecyclerView.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructs an ImageViewHolder using the given itemView.
         *
         * @param itemView The view for this ViewHolder.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
        }
    }
}
