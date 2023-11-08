package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class DateFilter extends Filter {
    private Date startDate;
    private Date endDate;

    public DateFilter(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }



    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        return itemList.stream()
                .filter(item -> {
                    Date itemDate = item.getAcquisitionDate();
                    return itemDate != null && itemDate.after(startDate) && itemDate.before(endDate);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
