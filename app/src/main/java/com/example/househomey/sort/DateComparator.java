package com.example.househomey.sort;

import com.example.househomey.Item;

import java.util.Comparator;

/**
 * Comparator for the an Item that compares items based on their acquisiton date
 * @author Sami Jagirdar
 */
public class DateComparator implements Comparator<Item> {

    /**
     * Compares 2 items by comparing their dates
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1 if o1 was acquired before o2, 0 if acquired at the same time, -1 otherwise
     */
    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getAcquisitionDate().compareTo(o2.getAcquisitionDate());
    }
}
