package com.example.househomey.Filters;


/**
 * Callback interface for handling filter changes between fragments.
 */
public interface FilterCallback {
    /**
     * Called when a filter is applied or modified.
     * @param filterType  The type of filter being applied (e.g., "MAKE").
     * @param filterValue The value or criteria for the filter.
     */
    void onFilterApplied(String filterType, String filterValue);
    // TODO: method for when rest applied
}