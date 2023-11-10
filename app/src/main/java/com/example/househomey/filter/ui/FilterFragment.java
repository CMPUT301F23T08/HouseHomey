package com.example.househomey.filter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.househomey.filter.model.FilterCallback;

/**
 * Abstract class for creating filter fragments.
 */
public abstract class FilterFragment extends DialogFragment {
    /**
     * Constructs a new FilterFragment.
     * title         The title of the filter dialog.
     * contentView   The content view of the filter dialog.
     * filterCallback The callback interface for handling filter changes.
     */
    protected String title;
    protected View contentView;
    protected FilterCallback filterCallback;

    /**
     * Constructs a new FilterFragment with the provided title, content view, and filter callback.
     * @param title           The title of the filter dialog.
     * @param contentView     The content view of the filter dialog.
     * @param filterCallback  The callback interface for handling filter changes.
     */
    public FilterFragment(String title, View contentView, FilterCallback filterCallback) {
        this.title = title;
        this.contentView = contentView;
        this.filterCallback = filterCallback;
    }

    /**
     * Called to create and return the filter dialog.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The filter dialog to be displayed.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogInterface.OnClickListener onApplyListener = (dialog, which) -> getFilterInput();

        DialogInterface.OnClickListener onResetListener = (dialog, which) -> resetFilter();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(contentView)
                .setTitle(title)
                .setNegativeButton("Reset", onResetListener) // TODO: Logic for how resetting filters work
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Apply", onApplyListener)
                .create();
    }

    /**
     * These methods should be implemented in subclasses to define filter-specific logic
     * e.g. date, make, keywords, and tag filters.
     */
    public abstract void getFilterInput();
    public abstract void resetFilter();
}
