package com.example.househomey.Filters;

import android.view.View;
import android.widget.EditText;

import com.example.househomey.R;

public class MakeFilterFragment extends FilterFragment {

    public MakeFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    @Override
    public void getFilterProps() {
        String makeValue = ((EditText) contentView.findViewById(R.id.make_filter)).getText().toString();
        filterCallback.onFilterApplied("MAKE", makeValue);
        dismiss();
    }
}
