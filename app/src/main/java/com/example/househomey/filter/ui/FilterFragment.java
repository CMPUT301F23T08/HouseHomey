package com.example.househomey.filter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;

/**
 * Abstract class for creating filter fragments.
 */
public abstract class FilterFragment extends DialogFragment {
    /**
     * Constructs a new FilterFragment.
     * contentView   The content view of the filter dialog.
     * filterCallback The callback interface for handling filter changes.
     */
    protected View contentView;
    protected FilterCallback filterCallback;

    /**
     * Called to create and return the filter dialog builder.
     * @return The builder for filter dialog to be displayed.
     */
    public AlertDialog.Builder createBuilder() {
        DialogInterface.OnClickListener onApplyListener = (dialog, which) -> getFilterInput();

        DialogInterface.OnClickListener onResetListener = (dialog, which) -> resetFilter();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setNegativeButton("Reset", onResetListener)
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Apply", onApplyListener);
    }

    /**
     * Customizes the appearance of buttons button in the associated AlertDialog.
     */
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        }
    }

    /**
     * These methods should be implemented in subclasses to define filter-specific logic
     * e.g. date, make, keywords, and tag filters.
     */
    public abstract void getFilterInput();
    public abstract void resetFilter();
}
