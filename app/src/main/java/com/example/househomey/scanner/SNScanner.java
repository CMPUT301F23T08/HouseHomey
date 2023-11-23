package com.example.househomey.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.househomey.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class SNScanner {

    public static String serialNumFromImgPath(Context context, String imagePath) {
        String serialNum = "";

        try {
            serialNum = scanInputImage(InputImage.fromFilePath(context, Uri.parse(imagePath)));
        } catch (IOException e) {
            Log.e("Scanner", "Failed to load Image from local file");
        }

        return serialNum;
    }

    private static String scanInputImage(InputImage image) {
        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(visionText -> {
                            String resultText = selectBestLine(visionText);
                            Log.i("Scanner", resultText);
                        })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    // ...
                                });

        return "";
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
