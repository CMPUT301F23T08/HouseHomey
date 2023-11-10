package com.example.househomey;

import com.example.househomey.filter.model.MakeFilter;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MakeFilterTest {

    private MakeFilter makeFilter;
    private ArrayList<Item> items;

    @Before
    public void setUp() {
        makeFilter = new MakeFilter("Sony");
        items = new ArrayList<>();

        // Create item data for Sony items
        items.add(createTestItem("1", "Sony", "Sony Camera", "499.99"));
        items.add(createTestItem("3", "Sony", "Sony TV", "999.99"));

        // Create item data for a non-Sony item
        items.add(createTestItem("2", "Samsung", "Samsung Monitor", "399.99"));
    }

    public Item createTestItem(String id, String make, String description, String cost) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("make", make);
        itemData.put("description", description);
        itemData.put("cost", cost);
        // Simulate the current date for acquisitionDate
        itemData.put("acquisitionDate", new Timestamp(new Date()));
        return new Item(id, itemData);
    }

    @Test
    public void testFilterListWithMatchingMake() {
        ArrayList<Item> filteredItems = makeFilter.filterList(items);
        assertEquals("Filtered list should only contain Sony items", 2, filteredItems.size());
        assertTrue("All items in the filtered list should be Sony", filteredItems.stream().allMatch(item -> "Sony".equals(item.getMake())));
    }

    @Test
    public void testFilterListWithNoMatchingMake() {
        makeFilter = new MakeFilter("Panasonic");
        ArrayList<Item> filteredItems = makeFilter.filterList(items);
        assertTrue("Filtered list should be empty as there are no Panasonic items", filteredItems.isEmpty());
    }

    @Test
    public void testFilterListWithNullMake() {
        makeFilter = new MakeFilter(null);
        ArrayList<Item> filteredItems = makeFilter.filterList(items);
        assertEquals("Filtered list should be unchanged when makeToFilterBy is null", items, filteredItems);
    }

    @Test
    public void testFilterListWithEmptyMake() {
        makeFilter = new MakeFilter("");
        ArrayList<Item> filteredItems = makeFilter.filterList(items);
        assertEquals("Filtered list should be unchanged when makeToFilterBy is empty", items, filteredItems);
    }

    @Test(expected = NullPointerException.class)
    public void testFilterListWithNullItemList() {
        makeFilter.filterList(null);
    }

    @Test
    public void testFilterListWithEmptyItemList() {
        ArrayList<Item> filteredItems = makeFilter.filterList(new ArrayList<>());
        assertTrue("Filtered list should be empty when input list is empty", filteredItems.isEmpty());
    }
}

