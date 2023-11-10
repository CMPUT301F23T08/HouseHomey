package com.example.househomey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemTest {
    private final Date startDate = new Date();
    private final String id = "test";
    private Map<String, Object> inputMap;

    private Map<String, Object> requiredInputMap() {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("description", "Glass");
        inputMap.put("acquisitionDate", new Timestamp(startDate));
        inputMap.put("cost", "47.00");
        return inputMap;
    }

    @Before
    public void setUp() {
        inputMap = requiredInputMap();
    }
    @Test
    public void testRequiredConstructor() {
        Item newItem = new Item(id, inputMap);

        assertEquals(id, newItem.getId());
        assertEquals(inputMap.get("description"), newItem.getDescription());
        assertEquals(((Timestamp)inputMap.get("acquisitionDate")).toDate(), newItem.getAcquisitionDate());
        assertEquals(new BigDecimal((String) inputMap.get("cost")), newItem.getCost());
        assertEquals("", newItem.getMake());
        assertEquals("", newItem.getModel());
        assertEquals("", newItem.getSerialNumber());
        assertEquals("", newItem.getComment());
    }

    @Test
    public void testAllFieldsConstructor() {
        inputMap.put("make", "Mikasa");
        inputMap.put("model", "Tall glass");
        inputMap.put("serialNumber", "123456");
        inputMap.put("comment", "chipped");
        Item newItem = new Item(id, inputMap);

        assertEquals(inputMap.get("make"), newItem.getMake());
        assertEquals(inputMap.get("model"), newItem.getModel());
        assertEquals(inputMap.get("serialNumber"), newItem.getSerialNumber());
        assertEquals(inputMap.get("comment"), newItem.getComment());
    }

    @Test
    public void testMissingId() {
        assertThrows(NullPointerException.class, () -> new Item(null, inputMap));
    }

    @Test
    public void testMissingDescription() {
        inputMap.remove("description");
        assertThrows(NullPointerException.class, () -> new Item(id, inputMap));
    }

    @Test
    public void testMissingAcquisitionDate() {
        inputMap.remove("acquisitionDate");
        assertThrows(NullPointerException.class, () -> new Item(id, inputMap));
    }

    @Test
    public void testMissingCost() {
        inputMap.remove("cost");
        assertThrows(NullPointerException.class, () -> new Item(id, inputMap));
    }

    @Test
    public void testGetData(){
        inputMap.put("make", "Mikasa");
        inputMap.put("model", "Tall glass");
        inputMap.put("serialNumber", "123456");
        inputMap.put("comment", "chipped");
        Item item = new Item(id, inputMap);
        assertEquals(item.getData(), inputMap);
    }

}
