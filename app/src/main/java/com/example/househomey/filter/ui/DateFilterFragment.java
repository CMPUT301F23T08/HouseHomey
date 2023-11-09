package com.example.househomey.filter.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.DateFilter;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Fragment for applying the date filter
 * @author Matthew Neufeld
 */
public class DateFilterFragment extends FilterFragment {

    private TextInputEditText startDateTextView;
    private TextInputEditText endDateTextView;
    private Date startDate;
    private Date endDate;

    /**
     * Constructor for DateFilterFragment
     * @param title title of date filter fragment
     * @param contentView content view of date filter fragment
     * @param filterCallback callback interface for handling filter changes
     */
    public DateFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    /**
     * Creates dialog for date filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for date filter fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog builder = super.onCreateDialog(savedInstanceState);

        startDateTextView = setDateTextView(contentView, R.id.start_date_filter, R.id.start_date_layout);
        endDateTextView = setDateTextView(contentView, R.id.end_date_filter, R.id.end_date_layout);

        return builder;

    }

    /**
     * sets dateTextView based on datePicker and whether user has picked start or end date
     * @param contentView content view of date filter fragment
     * @param dateViewId id of the date text view
     * @param layoutId id of layout associated with  date text view
     * @return TextInputEditText for the date selected
     */
    private TextInputEditText setDateTextView(View contentView, int dateViewId, int layoutId) {

        TextInputEditText dateTextView = contentView.findViewById(dateViewId);

        MaterialDatePicker<Long> datePicker = FragmentUtils.createDatePicker();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            dateTextView.setText(dateFormat.format(date));
            ((TextInputLayout) contentView.findViewById(layoutId)).setError(null);
            setStartOrEndDate(dateTextView, date);
        });

        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
        return dateTextView;
    }

    /**
     * sets the start or end date depending on which text view user is choosing
     * @param dateTextView text view for selected date
     * @param date user selected date
     */
    private void setStartOrEndDate(EditText dateTextView, Date date) {
        if (dateTextView.getId() == R.id.start_date_filter) {
            startDate = date;
        } else if (dateTextView.getId() == R.id.end_date_filter) {
            endDate = date;
        }
    }

    /**
     * Gets the filter input (start and end date) and creates callback on the DateFilter
     */
    @Override
    public void getFilterInput() {
        DateFilter dateFilter = new DateFilter(startDate, endDate);
        filterCallback.onFilterApplied(dateFilter);
        dismiss();
    }
}
