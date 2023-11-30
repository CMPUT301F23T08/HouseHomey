package com.example.househomey.filter.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.MakeFilter;

public class TagFilterFragment extends FilterFragment {
    /**
     * Creates dialog for tag filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for tag filter fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_tags, null);

        Dialog dialog = createBuilder().setTitle("Modify Tag Filter").setView(contentView).create();

        filterCallback = getArguments().getSerializable("callback", FilterCallback.class);

        return dialog;
    }

    @Override
    public void getFilterInput() {
        // TODO: get the filter properties from fragment inputs
    }

    @Override
    public void resetFilter() {
        // TODO: logic for resetting current filter
    }
}
