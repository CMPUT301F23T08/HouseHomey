package com.example.househomey.scanner;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class SNImageScanner extends ImageScanner {
    private Context context;
    private OnImageScannedListener listener;
    public interface OnImageScannedListener {
        void onSNScanningComplete(String serialNumber);
    }

    /**
     * Constructs a new SNImageScanner
     * @param context The context of this scanner
     * @param listener The listener to notify when scanning complete
     */
    public SNImageScanner(Context context, OnImageScannedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Scans the given image using google MLKit
     * @param imageUri Image to scan
     */
    @Override
    public void scanImage(String imageUri) {
        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        try {
            recognizer.process(InputImage.fromFilePath(context, Uri.parse(imageUri)))
                    .addOnSuccessListener(visionText -> {
                        String resultText = selectBestLine(visionText);
                        Log.i("Scanner", resultText);
                        listener.onSNScanningComplete(resultText);
                    })
                    .addOnFailureListener(
                            e -> Log.e("Scanner", "Failed to load Image from local file"));
        } catch (IOException e) {
            Log.e("Scanner", "Failed to load Image from local file");
        }
    }

    /**
     * Selects the best line from MLKit text by choosing the lowest, longest line of numbers
     * @param text The MLKit text to select the number from
     * @return the chosen serial number
     */
    private static String selectBestLine(Text text) {
        String bestElement = "";

        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements())
                    // Serial numbers are usually at the bottom
                    if (element.getText().length() >= bestElement.length())
                        bestElement = element.getText();
            }
        }
        return bestElement;
    }

}
