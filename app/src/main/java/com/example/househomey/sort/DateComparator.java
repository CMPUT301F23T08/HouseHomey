package com.example.househomey.sort;

import com.example.househomey.Item;

import java.util.Comparator;

public class DateComparator implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getAcquisitionDate().compareTo(o2.getAcquisitionDate());
    }
}
