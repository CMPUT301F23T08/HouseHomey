package com.example.househomey;

import static org.junit.Assert.assertEquals;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemTest {
    private Date startDate = new Date();

    private Map<String, Object> requiredInputMap() {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("description", "Glass");
        inputMap.put("acquisitionDate", new Timestamp(startDate));
        inputMap.put("cost", "47.00");
        return inputMap;
    }
    @Test
    public void testRequiredConstructor() {
        Map<String, Object> inputMap = requiredInputMap();
        String id = "test";
        Item newItem = new Item(id, inputMap);

        assertEquals(id, newItem.id);
        assertEquals(inputMap.get("description"), newItem.getDescription());
        assertEquals(((Timestamp)inputMap.get("acquisitionDate")).toDate(), newItem.getAcquisitionDate());
        assertEquals(new BigDecimal((String) inputMap.get("cost")), newItem.getCost());
    }
}
