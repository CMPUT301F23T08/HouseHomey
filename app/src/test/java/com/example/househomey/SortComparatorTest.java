package com.example.househomey;

import com.example.househomey.sort.CostComparator;
import com.example.househomey.sort.DateComparator;
import com.example.househomey.sort.DescriptionComparator;
import com.example.househomey.sort.MakeComparator;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SortComparatorTest {
    private final String id1 = "test1";
    private final String id2 = "test2";
    private final String id3 = "test3";
    private ArrayList<Item> testList;
    private Item item1;
    private Item item2;
    private Item item3;


    private ArrayList<Item> testItems() {


        Map<String, Object> input1 = new HashMap<>();
        input1.put("description", "C");
        input1.put("acquisitionDate", new Timestamp(new Date(2023, 1, 1)));
        input1.put("cost", "20.00");
        input1.put("make", "make C");
        Item item1 = new Item(id1, input1);

        Map<String, Object> input2 = new HashMap<>();
        input2.put("description", "A");
        input2.put("acquisitionDate", new Timestamp(new Date(2023, 1, 3)));
        input2.put("cost", "10.00");
        input2.put("make", "make B");
        Item item2 = new Item(id2, input2);

        Map<String, Object> input3 = new HashMap<>();
        input3.put("description", "B");
        input3.put("acquisitionDate", new Timestamp(new Date(2023, 1, 2)));
        input3.put("cost", "30.00");
        input3.put("make", "make A");
        Item item3 = new Item(id3, input3);

        ArrayList<Item> testList = new ArrayList<>();
        testList.add(item1);
        testList.add(item2);
        testList.add(item3);

        return testList;

    }

    @Before
    public void setup() {
        testList = testItems();
        item1 = testList.get(0);
        item2 = testList.get(1);
        item3 = testList.get(2);
    }

    @Test
    public void testComparator() {

        // Create a list of items in an unsorted order
        ArrayList<Item> unsortedList = new ArrayList<>();
        unsortedList.add(item1);
        unsortedList.add(item2);
        unsortedList.add(item3);

        // Test CostComparator
        testComparator(new CostComparator(), unsortedList, item2, item1, item3);

        // Test DateComparator
        testComparator(new DateComparator(), unsortedList, item1, item3, item2);

        // Test DescriptionComparator
        testComparator(new DescriptionComparator(), unsortedList, item2, item3, item1);

        // Test MakeComparator
        testComparator(new MakeComparator(), unsortedList, item3, item2, item1);
    }

    private void testComparator(Comparator<Item> comparator, List<Item> unsortedList, Item... expectedOrder) {
        // Sort the list using the provided comparator
        Collections.sort(unsortedList, comparator);

        // Check if the items are now in the expected order
        for (int i = 0; i < expectedOrder.length; i++) {
            assertEquals("Item at index " + i + " should be " + expectedOrder[i].getDescription(), expectedOrder[i], unsortedList.get(i));
        }
    }
}
