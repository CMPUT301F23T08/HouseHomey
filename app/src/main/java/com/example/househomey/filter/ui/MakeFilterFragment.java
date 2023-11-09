package com.example.househomey.filter.ui;

import android.view.View;
import android.widget.EditText;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.MakeFilter;

/**
 * Fragment for applying "Make" filter criteria.
 */
public class MakeFilterFragment extends FilterFragment {
    /**
     * Constructs a new MakeFilterFragment.
     * @param title         The title of the "Make" filter dialog.
     * @param contentView   The content view of the "Make" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     */
    public MakeFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    /**
     * Extracts the "Make" filter edit text input and applies the filter.
     */
    @Override
    public void getFilterInput() {
        String makeValue = ((EditText) contentView.findViewById(R.id.make_filter)).getText().toString();
        MakeFilter makeFilter = new MakeFilter(makeValue);
        filterCallback.onFilterApplied(makeFilter);
        dismiss();
    }

    /**
     * Deletes the currently applied "Make" filter on the home fragment.
     */
    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(MakeFilter.class);
    }
}
