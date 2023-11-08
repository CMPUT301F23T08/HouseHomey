package com.example.househomey.filter.model;


/**
 * Callback interface for handling filter changes between fragments.
 * @see com.example.househomey.HomeBaseStateFragment
 */
public interface FilterCallback {
    void onFilterApplied(Filter filter);
    // TODO: method for when reset applied
}