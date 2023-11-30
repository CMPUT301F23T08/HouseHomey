package com.example.househomey.filter.model;


import java.io.Serializable;

/**
 * Callback interface for handling filter changes between fragments.
 * @see com.example.househomey.HomeFragment
 */
public interface FilterCallback extends Serializable {
    void onFilterApplied(Filter filter);
    void onFilterReset(Filter filter);
}