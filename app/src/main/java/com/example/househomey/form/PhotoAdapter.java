package com.example.househomey.form;

import static com.example.househomey.utils.FragmentUtils.isValidUUID;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.househomey.MainActivity;
import com.example.househomey.R;

import java.util.List;
import java.util.Objects;

/**
 * Custom adapter for displaying and adding photos to a RecyclerView.
 *
 * @author Owen Cooke
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HOLDER_SIZE_DP = 87;
    private static final int VIEW_TYPE_IMAGE = 1;
    private final Context context;
    private final List<String> imageUris;
    private final OnButtonClickListener onButtonClickListener;

    /**
     * Constructs a PhotoAdapter with the given context and a list of image URIs to display.
     *
     * @param context   The context.
     * @param imageUris The list of image URIs.
     */
    public PhotoAdapter(Context context, List<String> imageUris, OnButtonClickListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.onButtonClickListener = listener;
    }

    /**
     * Called when this adapter is attached to a RecyclerView.
     * Makes a call to adjust the column count within the RecyclerView
     *
     * @param recyclerView The RecyclerView to which this adapter is attached.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setColumnCount(recyclerView);
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate((viewType == VIEW_TYPE_IMAGE) ?
                        R.layout.gallery_photo_with_delete :
                        R.layout.add_photo_button, parent, false);
        resizeViews(view, viewType);
        return (viewType == VIEW_TYPE_IMAGE) ?
                new ImageViewHolder(view) :
                new RecyclerView.ViewHolder(view) {
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
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            loadIntoImageView(viewHolder.imageView, imageUris.get(position));
            viewHolder.deleteButton.setOnClickListener(v -> onButtonClickListener.onDeleteButtonClicked(holder.getAdapterPosition()));
        } else {
            holder.itemView.setOnClickListener(v -> onButtonClickListener.onAddButtonClicked());
        }
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
     * Loads the image defined by a URI into an ImageView using Glide.
     *
     * @param imageView The ImageView to load the image into.
     * @param imagePath The string URI of the image.
     */
    private void loadIntoImageView(ImageView imageView, String imagePath) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).asDrawable();
        if (isValidUUID(imagePath)) {
            // Cloud Storage UUID, fetch from Firebase
            requestBuilder.load(((MainActivity) context).getImageRef(imagePath));
        } else {
            // Local file URI, load directly
            requestBuilder.load(imagePath);
        }
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
    }

    /**
     * Adjusts the column count of the associated GridLayoutManager based on the screen size.
     * Ensures images don't overlap each other in the rows.
     *
     * @param recyclerView The RecyclerView to which this adapter is attached.
     */
    private void setColumnCount(RecyclerView recyclerView) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = Math.max((int) (dpWidth / HOLDER_SIZE_DP), 1);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        Objects.requireNonNull(layoutManager).setSpanCount(spanCount);
    }

    /**
     * Resizes the given parent container view and its inner elements.
     * Used to align the delete image button to the top right corner of an image.
     *
     * @param view     The parent view containing the views to be resized.
     * @param viewType The type of the view, used to determine which type of view to resize.
     */
    private void resizeViews(View view, int viewType) {
        // Set the size of the holder (which contains the image and delete button)
        int holderSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                HOLDER_SIZE_DP,
                view.getContext().getResources().getDisplayMetrics()
        );
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = holderSize;
        layoutParams.height = holderSize;
        view.setLayoutParams(layoutParams);

        // Set the size of the image (and add button block)
        int photoSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                HOLDER_SIZE_DP - 9,
                view.getContext().getResources().getDisplayMetrics()
        );
        ImageView imageView = view.findViewById((viewType == VIEW_TYPE_IMAGE) ? R.id.gallery_image_view : R.id.add_photo_button);
        ViewGroup.LayoutParams photoLayoutParams = imageView.getLayoutParams();
        photoLayoutParams.width = photoSize;
        photoLayoutParams.height = photoSize;
        imageView.setLayoutParams(photoLayoutParams);
    }

    /**
     * Interface for callbacks when buttons within the photo adapter are clicked.
     */
    public interface OnButtonClickListener {
        void onAddButtonClicked();

        void onDeleteButtonClicked(int position);
    }

    /**
     * ViewHolder for displaying images in the RecyclerView.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        /**
         * Constructs an ImageViewHolder using the given itemView.
         *
         * @param itemView The view for this ViewHolder.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
            deleteButton = itemView.findViewById(R.id.delete_photo_button);
        }
    }
}
