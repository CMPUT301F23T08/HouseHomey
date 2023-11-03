package com.example.househomey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;


public class AddItemFragment extends Fragment {
    private TextInputEditText dateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        // Show DatePicker when date field selected
        dateTextView = rootView.findViewById(R.id.add_item_date);
        dateTextView.setOnClickListener(v -> showDatePicker());
        return rootView;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> dateTextView.setText(datePicker.getHeaderText()));

        datePicker.show(getParentFragmentManager(), "Date Picker");
    }
}
