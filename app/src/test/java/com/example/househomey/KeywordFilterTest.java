package com.example.househomey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.househomey.filter.model.KeywordFilter;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KeywordFilterTest {
    private KeywordFilter keywordFilter;
    private ArrayList<Item> items;
    private ArrayList<String> keywords = new ArrayList<>();

    @Before
    public void setUp() {
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

    public String getTimeStamp() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Define a custom date and time format using DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the LocalDateTime using the defined formatter
        String timestamp = now.format(formatter);
        return timestamp;
    }

    @Test
    public void testFilterListWithMatchingKeywords() {
        keywords.add("Sony");
        keywordFilter = new KeywordFilter(keywords);
        ArrayList<Item> filteredItems = keywordFilter.filterList(items);
        assertEquals("Filtered list should only contain Sony items", 2, filteredItems.size());
        assertTrue("All items in the filtered list should be Sony", filteredItems.stream().allMatch(item -> "Sony".equals(item.getMake())));
    }

    @Test
    public void testFilterListWithNoMatchingKeywords() {
        keywords.add("Panasonic");
        keywordFilter = new KeywordFilter(keywords);
        ArrayList<Item> filteredItems = keywordFilter.filterList(items);
        assertTrue("Filtered list should be empty as there are no Panasonic items", filteredItems.isEmpty());
    }

    @Test
    public void testFilterListWithEmptyKeywords() {
        keywords.add("");
        keywordFilter = new KeywordFilter(keywords);
        ArrayList<Item> filteredItems = keywordFilter.filterList(items);
        assertEquals("Filtered list should be unchanged when makeToFilterBy is empty", items, filteredItems);
    }

    @Test
    public void testFilterListWithEmptyItemList() {
        keywords.add("Sony");
        keywordFilter = new KeywordFilter(keywords);
        ArrayList<Item> filteredItems = keywordFilter.filterList(new ArrayList<>());
        assertTrue("Filtered list should be empty when input list is empty", filteredItems.isEmpty());
    }

}
