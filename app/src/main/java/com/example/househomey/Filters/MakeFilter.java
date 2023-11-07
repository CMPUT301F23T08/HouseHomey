package com.example.househomey.Filters;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MakeFilter extends Filter {
    private String makeToFilterBy;

    public MakeFilter(String make) {
        this.makeToFilterBy = make;
    }

    @Override
    public ArrayList<Item> filterList(ArrayList<Item> originalList) {
        return originalList.stream()
                .filter(item -> item.getMake().equals(makeToFilterBy))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}