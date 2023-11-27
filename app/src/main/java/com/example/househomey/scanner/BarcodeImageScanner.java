package com.example.househomey.scanner;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class BarcodeImageScanner extends ImageScanner{

    private InputImage image;
    private BarcodeScanner scanner;
    private Context context;

    private final OnBarcodeScannedListener listener;

    public BarcodeImageScanner(Context context, OnBarcodeScannedListener listener) {
        this.context = context;
        this.listener = listener;

    }
    @Override
    public void scanImage(String imageUri) {
        try {
            image = InputImage.fromFilePath(context, Uri.parse(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        String description = "";
                        for (Barcode barcode: barcodes) {
//                      TODO Need to implement properly choosing a barcode and its extracted value
                            String currentValue = barcode.getRawValue();
                            if (currentValue!=null) {
                                if (currentValue.compareTo(description)>0) {
                                    Log.i("Scanner", currentValue);
                                    description = currentValue;
                                }
                            }
                        }
                        Toast.makeText(context.getApplicationContext(),
                                description,
                                Toast.LENGTH_SHORT).show();
                        listener.onBarcodeOKPressed(description);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Log.e("Scanner","faied to scan");
                    }
                });

    }

    public interface OnBarcodeScannedListener {
        void onBarcodeOKPressed(String description);
    }
}
