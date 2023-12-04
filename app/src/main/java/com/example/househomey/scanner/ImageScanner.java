package com.example.househomey.scanner;

/**
 * An abstract class defining an ImageScanner. All children must have the public scanImage()
 * function
 * @author Lukas Bonkowski
 * @see ScannerPickerDialog
 */
public abstract class ImageScanner {

    /**
     * Scans a given image
     * @param imageUri Image to scan
     */
    public abstract void scanImage(String imageUri);
}
