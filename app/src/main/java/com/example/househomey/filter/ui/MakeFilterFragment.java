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
    protected MakeFilter makeFilter;

    /**
     * Constructs a new MakeFilterFragment when a makeFilter is already applied.
     * @param title         The title of the "Make" filter dialog.
     * @param contentView   The content view of the "Make" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     * @param makeFilter The existing applied filter.
     */
    public MakeFilterFragment(String title, View contentView, FilterCallback filterCallback, MakeFilter makeFilter) {
        super(title, contentView, filterCallback);
        this.makeFilter = makeFilter;
        autoFillLastFilter(makeFilter.makeToFilterBy);
    }

    /**
     * Constructs a new MakeFilterFragment when no make filter has been applied yet.
     * @param title         The title of the "Make" filter dialog.
     * @param contentView   The content view of the "Make" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     */
    public MakeFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    /**
     * Fills in the make input field in this fragment with the currently applied make filter.
     * @param makeToFilterBy The string to filter make by
     */
    public void autoFillLastFilter(String makeToFilterBy) {
        EditText editText = contentView.findViewById(R.id.make_filter);
        editText.setText(makeToFilterBy);
    }

    /**
     * Extracts the "Make" filter edit text input and applies the filter.
     */
    @Override
    public void getFilterInput() {
        String makeValue = ((EditText) contentView.findViewById(R.id.make_filter)).getText().toString();
        MakeFilter makeFilter = new MakeFilter(makeValue);
        this.makeFilter = makeFilter;
        filterCallback.onFilterApplied(this.makeFilter);
        dismiss();

    }

    /**
     * Deletes the currently applied "Make" filter on the home fragment.
     */
    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.makeFilter);
        dismiss();
    }

}
