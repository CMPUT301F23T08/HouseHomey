package com.example.househomey.filter.model;


import com.example.househomey.home.HomeFragment;

import java.io.Serializable;

/**
 * Callback interface for handling filter changes between fragments.
 * @see HomeFragment
 */
public interface FilterCallback extends Serializable {
    void onFilterApplied(Filter filter);
    void onFilterReset(Filter filter);
}