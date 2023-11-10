package com.example.househomey.sort;

import com.example.househomey.Item;

import java.util.Comparator;

public class DescriptionComparator implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getDescription().compareTo(o2.getDescription());
    }
}
