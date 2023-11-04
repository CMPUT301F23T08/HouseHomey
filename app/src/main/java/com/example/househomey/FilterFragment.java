package com.example.househomey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class FilterFragment extends DialogFragment {

    protected String title;
    protected View contentView;

    public FilterFragment(String title, View contentView) {
        this.title = title;
        this.contentView = contentView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogInterface.OnClickListener onApplyListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyFilter();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(contentView)
                .setTitle(title)
                .setNegativeButton("Reset", null) // TODO: Logic for how resetting filters work
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Apply", onApplyListener)
                .create();
    }

    // Abstract method for filter-specific logic (dates, make, keywords, tags)
    public abstract void applyFilter();
}
