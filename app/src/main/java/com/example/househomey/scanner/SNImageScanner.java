package com.example.househomey.scanner;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for handling the scanning of serial numbers and updating item information accordingly
 * @author Lukas Bonkowski
 * @see ImageScanner
 */
public class SNImageScanner extends ImageScanner {
    private Context context;
    private OnImageScannedListener listener;

    public interface OnImageScannedListener {
        void onSNScanningComplete(String serialNumber);
    }

    /**
     * Constructs a new SNImageScanner
     *
     * @param context  The context of this scanner
     * @param listener The listener to notify when scanning complete
     */
    public SNImageScanner(Context context, OnImageScannedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Scans the given image using google MLKit
     *
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
                        new AlertDialog.Builder(context)
                                .setTitle("Serial number scanned: ")
                                .setMessage(resultText +
                                        "\n \nSet as serial number?")
                                .setPositiveButton("YES", (dialog, which) -> {
                                    listener.onSNScanningComplete(resultText);
                                })
                                .setNegativeButton("NO", null)
                                .show();

                    })
                    .addOnFailureListener(
                            e -> Log.e("Scanner", "Failed to load Image from local file"));
        } catch (IOException e) {
            Log.e("Scanner", "Failed to load Image from local file");
        }
    }

    /**
     * Selects the best line from MLKit text by choosing the text with
     * the most digits
     *
     * @param text The MLKit text to select the number from
     * @return the chosen serial number
     */
    private static String selectBestLine(Text text) {

        String bestElement = "";
        List<String> strings = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d");
        int maxTotalDigits = 0;

        //Get all the text elements/words from the image as strings
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements())
                    strings.add(element.getText());
            }
        }

        //Select the string that has the most numbers in it
        for (String str : strings) {
            Matcher matcher = pattern.matcher(str);
            int totalDigits = 0;
            while (matcher.find()) {
                totalDigits++;
            }
            if (totalDigits > maxTotalDigits) {
                maxTotalDigits = totalDigits;
                bestElement = str;
            }
        }
        return bestElement;
    }

}