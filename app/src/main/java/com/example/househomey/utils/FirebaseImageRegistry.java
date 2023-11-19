package com.example.househomey.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import androidx.annotation.NonNull;

/**
 * Required for loading Firebase Cloud Storage images directly into ImageViews using Glide.
 * It registers the {@link com.firebase.ui.storage.images.FirebaseImageLoader} to handle StorageReference objects.
 * Reference: https://github.com/firebase/FirebaseUI-Android/blob/master/app/src/main/java/com/firebase/uidemo/storage/MyAppGlideModule.java
 */
@GlideModule
public class FirebaseImageRegistry extends AppGlideModule {

    /**
     * Registers components needed for loading images from Firebase Cloud Storage using Glide.
     *
     * @param context  The application context.
     * @param glide    The Glide instance.
     * @param registry The Glide registry to register components.
     */
    @Override
    public void registerComponents(@NonNull Context context,
                                   @NonNull Glide glide,
                                   @NonNull Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}
