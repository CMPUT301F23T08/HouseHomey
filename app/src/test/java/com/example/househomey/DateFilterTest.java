package com.example.househomey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.househomey.filter.model.DateFilter;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateFilterTest {

    private DateFilter dateFilter;
    private ArrayList<Item> items;

    @Before
    public void setUp() {
        dateFilter = new DateFilter();
        items = new ArrayList<>();

        items.add(createTestItem("1", "Sony", "Sony Camera", "499.99", "09/01/2023"));
        items.add(createTestItem("2", "Samsung", "Samsung Monitor", "399.99", "10/01/2023"));
        items.add(createTestItem("3", "Sony", "Sony TV", "999.99", "11/01/2023"));

    }

    public Item createTestItem(String id, String make, String description, String cost, String date) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("make", make);
        itemData.put("description", description);
        itemData.put("cost", cost);
        // Simulate the current date for acquisitionDate
        itemData.put("acquisitionDate", new Timestamp(new Date()));
        return new Item(id, itemData);
    }

    @Test
    public void testFilterListWithValidDateRange() {
        dateFilter.setStartDate(new Date(0));  // January 1, 1970
        dateFilter.setEndDate(new Date());     // Current date
        ArrayList<Item> filteredItems = dateFilter.filterList(items);
        assertEquals(items.size(), filteredItems.size());
        assertTrue(items.containsAll(filteredItems));
    }

    @Test
    public void testFilterListWithStartDateOnly() {
        dateFilter.setStartDate(new Date(0));  // January 1, 1970
        ArrayList<Item> filteredItems = dateFilter.filterList(items);
        assertEquals(items.size(), filteredItems.size());
        assertTrue(items.containsAll(filteredItems));
    }

    @Test
    public void testFilterListWithEndDateOnly() {
        dateFilter.setEndDate(new Date());
        ArrayList<Item> filteredItems = dateFilter.filterList(items);
        assertEquals(items.size(), filteredItems.size());
        assertTrue(items.containsAll(filteredItems));
    }


    @Test
    public void testFilterListWithEmptyStartAndEndDate() {
        dateFilter.setStartDate(null);  // Empty start date
        dateFilter.setEndDate(null);    // Empty end date
        ArrayList<Item> filteredItems = dateFilter.filterList(items);
        assertEquals(items.size(), filteredItems.size());
        assertTrue(filteredItems.containsAll(items));
    }


    @Test
    public void testFilterListWithEmptyItemList() {
        ArrayList<Item> filteredItems = dateFilter.filterList(new ArrayList<>());
        assertTrue("Filtered list should be empty when input list is empty", filteredItems.isEmpty());
    }


}
