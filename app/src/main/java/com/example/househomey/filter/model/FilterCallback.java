package com.example.househomey.filter.model;


/**
 * Callback interface for handling filter changes between fragments.
 * @see com.example.househomey.HomeFragment
 */
public interface FilterCallback {
    void onFilterApplied(Filter filter);
    void onFilterReset(Class<?> filterClass);
}