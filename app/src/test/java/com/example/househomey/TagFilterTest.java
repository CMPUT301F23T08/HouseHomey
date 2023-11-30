package com.example.househomey;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.example.househomey.filter.model.TagFilter;
import com.example.househomey.tags.Tag;
import com.google.firebase.Timestamp;
import static org.junit.Assert.assertEquals;

public class TagFilterTest {
    private ArrayList<Item> itemList;

    @Before
    public void setUp() {
        itemList = new ArrayList<>();
        itemList.add(createItem("3", "Smartphone", "Electronics", BigDecimal.valueOf(799.99), "Samsung", "Galaxy", "789012", "Nice phone"));
        itemList.add(createItem("1", "Laptop", "Electronics", BigDecimal.valueOf(999.99), "Dell", "XPS", "123456", "Good laptop"));
        itemList.add(createItem("2", "Sofa", "Furniture", BigDecimal.valueOf(499.99), "", "", "", ""));
    }

    private Item createItem(String id, String description, String tagLabel, BigDecimal cost, String make, String model, String serialNumber, String comment) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", description);
        data.put("acquisitionDate", new Timestamp(new Date()));
        data.put("cost", cost.toString());
        data.put("make", make);
        data.put("model", model);
        data.put("serialNumber", serialNumber);
        data.put("comment", comment);

        Item item = new Item(id, data);
        item.addTag(new Tag(tagLabel, data));
        return item;
    }

    @Test
    public void testNoTagSelected() {
        Map<String, Boolean> tagSelectionMap = new HashMap<>();
        tagSelectionMap.put("Electronics", false);
        tagSelectionMap.put("Furniture", false);

        TagFilter tagFilter = new TagFilter(tagSelectionMap);

        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(3, filteredList.size());
    }

    @Test
    public void testOneTagSelected() {
        Map<String, Boolean> tagSelectionMap = new HashMap<>();
        tagSelectionMap.put("Electronics", true);
        tagSelectionMap.put("Furniture", false);

        TagFilter tagFilter = new TagFilter(tagSelectionMap);

        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(2, filteredList.size());
    }

    @Test
    public void testAllTagsSelected() {
        Map<String, Boolean> tagSelectionMap = new HashMap<>();
        tagSelectionMap.put("Electronics", true);
        tagSelectionMap.put("Furniture", true);

        TagFilter tagFilter = new TagFilter(tagSelectionMap);

        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(3, filteredList.size());
    }
}
