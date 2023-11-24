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

    public SNImageScanner(Context context, OnImageScannedListener listener) {
        this.context = context;
        this.listener = listener;
    }

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

    private static String selectBestLine(Text text) {
        String bestLine = "";

        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                // Convert each line into a string of numbers
                String lineText = line.getText().replaceAll("[^0-9]", "");

                // Serial numbers are usually at the bottom?
                if (lineText.length() >= bestLine.length())
                    bestLine = lineText;
            }
        }
        return bestLine;
    }

}
