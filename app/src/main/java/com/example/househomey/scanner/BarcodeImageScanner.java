package com.example.househomey.scanner;

import android.app.AlertDialog;
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

/**
 * A class for handling the scanning and decoding of barcodes
 * and updating item information accordingly
 * @author Sami
 */
public class BarcodeImageScanner extends ImageScanner{

    private InputImage image;
    private BarcodeScanner scanner;
    private Context context;
    private final OnBarcodeScannedListener listener;

    /**
     * Constructs a new BarcodeImageScanner
     * @param context context of this scanner
     * @param listener listener that handles the action when barcode is scanned
     */
    public BarcodeImageScanner(Context context, OnBarcodeScannedListener listener) {
        this.context = context;
        this.listener = listener;

    }

    /**
     * Scans image for barcodes using Google's MLKit API and handles information extraction
     * @param imageUri Image to scan
     */
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
                        selectProductInfoFromBarcode(barcodes,context);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(),
                                ("Failure in scanning barcode!"),
                                Toast.LENGTH_SHORT).show();
                        Log.e("Scanner","faied to scan");
                    }
                });

    }

    /**
     * Ensures that only 1 barcode was scanned and extracts the value encoded into the barcode
     * Depending on the value type, lets user set it as either item description or Serial Number
     * @param barcodes List of barcodes detected and scanned by the scanner
     * @param context context in which the scanner is being used in the app
     */
    private void selectProductInfoFromBarcode(List<Barcode> barcodes, Context context) {
        if (barcodes.size() == 0) {
            Toast.makeText(context.getApplicationContext(),
                    ("No barcodes were detected. Please use a different image."),
                    Toast.LENGTH_LONG).show();
        }
        else if (barcodes.size()==1){
            String productInfo = barcodes.get(0).getDisplayValue();

            if (barcodes.get(0).getValueType() == 7 || barcodes.get(0).getValueType() == 5) {
                new AlertDialog.Builder(context)
                        .setTitle("Item info from decoded barcode: ")
                        .setMessage(productInfo +
                                "\n \nDecoded value may be a serial number. Set as:")
                        .setPositiveButton("Description",(dialog,which)->{
                            listener.onBarcodeOKPressed(productInfo);
                        })
                        .setNegativeButton("Serial Number",(dialog,which)->{
                            listener.onSerialNumberOKPressed(productInfo);
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
            }
            else {
                new AlertDialog.Builder(context)
                        .setTitle("Item info from decoded barcode: ")
                        .setMessage(productInfo +
                                "\n \nSet info as item description?")
                        .setPositiveButton("YES",(dialog,which)->{
                            listener.onBarcodeOKPressed(productInfo);
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }

        }
        else {
            Toast.makeText(context.getApplicationContext(),
                    ("Multiple barcodes detected. Please choose an image with one barcode."),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Interface to handle how the decoded value of the barcode is used after scanning
     */
    public interface OnBarcodeScannedListener {
        void onBarcodeOKPressed(String description);
        void onSerialNumberOKPressed(String serialNumber);
    }
}
