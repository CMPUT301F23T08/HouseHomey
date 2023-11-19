package com.example.househomey.form;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.househomey.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A bottom sheet dialog fragment for capturing images from the camera or
 * selecting them from the user's photo gallery.
 *
 * @author Owen Cooke
 */
public class ImagePickerDialog extends BottomSheetDialogFragment {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private boolean fromGallery;
    private OnImagePickedListener onImagePickedListener;

    /**
     * Creates and returns the view hierarchy associated with this dialog.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the dialog's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_photo_choices, container, false);

        // Set up listeners to open the camera/gallery
        Button galleryButton = rootView.findViewById(R.id.gallery_button);
        Button cameraButton = rootView.findViewById(R.id.camera_button);
        galleryButton.setOnClickListener(v -> launchImageIntent(true));
        cameraButton.setOnClickListener(v -> launchImageIntent(false));
        initImageResultHandler();

        // Define listener for image result
        Fragment parent = requireParentFragment();
        if (parent instanceof OnImagePickedListener) {
            onImagePickedListener = (OnImagePickedListener) parent;
        } else {
            throw new ClassCastException(parent + " must implement OnImagePickedListener");
        }

        return rootView;
    }

    /**
     * Launches an image intent based on the user's choice of from the camera or gallery.
     *
     * @param fromGallery A boolean, true if the image should be picked from the gallery,
     *                    false for if the camera should be opened.
     */
    private void launchImageIntent(boolean fromGallery) {
        this.fromGallery = fromGallery;
        Intent imageIntent = fromGallery
                ? new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                : new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pickImageLauncher.launch(imageIntent);
    }

    /**
     * Initializes the activity result handler for image capture/picking.
     * Returns the image's URI to the listener via onImagePicked.
     */
    private void initImageResultHandler() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Uri imageUri;
            Intent data = result.getData();
            if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                if (fromGallery) {
                    imageUri = data.getData();
                } else {
                    // Get bitmap from camera and store in a temp file (to pass a URI for Firebase uploading)
                    Bitmap imageBitmap = data.getParcelableExtra("data", Bitmap.class);
                    File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = null;
                    try {
                        imageFile = File.createTempFile("image", ".jpg", storageDir);
                        FileOutputStream out = new FileOutputStream(imageFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (IOException e) {
                        Log.e("CAPTURE_IMG_ERR", e.toString());
                    }
                    imageUri = Uri.fromFile(imageFile);
                }
                // Pass the local image URI via listener
                if (onImagePickedListener != null && imageUri != null) {
                    onImagePickedListener.onImagePicked(imageUri.toString());
                }
            }
        });
    }

    /**
     * Interface definition for a callback when an image is picked.
     */
    public interface OnImagePickedListener {
        void onImagePicked(String imageUri);
    }
}

