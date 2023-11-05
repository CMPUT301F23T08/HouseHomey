package com.example.househomey.Filters;

import android.view.View;

public class MakeFilterFragment extends FilterFragment {

    public MakeFilterFragment(String title, View contentView) {
        super(title, contentView);
    }

    @Override
    public void applyFilter() {
        // String makeValue = ((EditText) contentView.findViewById(R.id.makeEditText)).getText().toString();
        filterCallback.onFilterApplied("MAKE", "Apple");
        dismiss();
    }
}
