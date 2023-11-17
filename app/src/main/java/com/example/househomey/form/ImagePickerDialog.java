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

import com.example.househomey.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePickerDialog extends BottomSheetDialogFragment {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private boolean fromGallery;
    private OnImagePickedListener onImagePickedListener;

    public void setOnImagePickedListener(OnImagePickedListener listener) {
        this.onImagePickedListener = listener;
    }

    public interface OnImagePickedListener {
        void onImagePicked(String imageUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_photo_choices, container, false);

        // Set up listeners to open the camera/gallery
        Button galleryButton = rootView.findViewById(R.id.gallery_button);
        Button cameraButton = rootView.findViewById(R.id.camera_button);
        galleryButton.setOnClickListener(v -> launchImageIntent(true));
        cameraButton.setOnClickListener(v -> launchImageIntent(false));
        initImageResultHandler();

        return rootView;
    }

    private void launchImageIntent(boolean fromGallery) {
        this.fromGallery = fromGallery;
        Intent imageIntent = fromGallery
                ? new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                : new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pickImageLauncher.launch(imageIntent);
    }

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
}

