package com.example.househomey.filter.ui;

import android.view.View;
import android.widget.EditText;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.DateFilter;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
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

    public TextInputEditText startDateTextView;
    public TextInputEditText endDateTextView;
    protected Date startDate;
    protected Date endDate;

    /**
     * Constructs new DateFilterFragment
     * @param title the title of the new date filter dialogue
     * @param contentView the content view of the date filter dialogue
     * @param filterCallback the callback interface for handling filter changes
     */
    public DateFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);

        startDateTextView = InitDateField(contentView, R.id.start_date_filter, R.id.start_date_layout);
        endDateTextView = InitDateField(contentView, R.id.end_date_filter, R.id.end_date_layout);
    }

    /**
     * Sets the TextView to the date selected by the user
     * @param contentView the content view of the date filter dialogue
     * @param dateViewId the ID of the view, will correspond to start or end date
     * @param layoutId the ID of the layout, will correspond to layout for start or end date
     * @return dateTextView: the
     */
    private TextInputEditText InitDateField(View contentView, int dateViewId, int layoutId) {

        // Getting the right TextInputEditText based on dateViewId
        TextInputEditText dateTextView = contentView.findViewById(dateViewId);

        // Setup calendar requirements and constraints
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis());
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // ensure proper format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // need to set time zone to prevent mismatched dates
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // set text view to the date given by the user and place in layout specified by layoutId
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            dateTextView.setText(dateFormat.format(date));
            ((TextInputLayout) contentView.findViewById(layoutId)).setError(null);
            // call setStartOrEndDate to determine which date was set (start or end)
            setStartOrEndDate(dateTextView, date);
        });

        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
        return dateTextView;
    }

    /**
     * Sets the start or end date depending on the id of the filter
     * @param dateTextView the text view that has received a date
     * @param date the date that was selected
     */
    private void setStartOrEndDate(EditText dateTextView, Date date) {
        if (dateTextView.getId() == R.id.start_date_filter) {
            startDate = date;
        } else if (dateTextView.getId() == R.id.end_date_filter) {
            endDate = date;
        }
    }

    /**
     * Takes startDate and endDate and applies DateFilter to them
     */
    @Override
    public void getFilterInput() {
        DateFilter dateFilter = new DateFilter(startDate, endDate);
        filterCallback.onFilterApplied(dateFilter);
        dismiss();
    }
}
