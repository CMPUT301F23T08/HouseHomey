package com.example.househomey.filter.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.filter.model.DateFilter;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.MakeFilter;

/**
 * Fragment for applying "Make" filter criteria.
 */
public class MakeFilterFragment extends FilterFragment {
    protected MakeFilter makeFilter;


    /**
     * Creates dialog for date filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for date filter fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_make, null);

        Dialog dialog = createBuilder().setTitle("Modify Make Filter").setView(contentView).create();

        filterCallback = getArguments().getSerializable("callback", FilterCallback.class);

        makeFilter = getArguments().getSerializable("filter", MakeFilter.class);
        if (makeFilter != null)
            autoFillLastFilter(makeFilter.makeToFilterBy);
        return dialog;
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
