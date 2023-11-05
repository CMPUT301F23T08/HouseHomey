package com.example.househomey;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Item {
    public String id;
    private String description;
    private Date acquisitionDate;
    private String make;
    private String model;
    private String serialNumber;
    private String comment;

    private BigDecimal cost;

    private Map<String, Object> data;

    public Item(String id, Map<String, Object> data) {
        this.data = data;
        this.id = id;
        this.description = (String) data.get("description");
        this.acquisitionDate = ((Timestamp) data.get("acquisitionDate")).toDate();

        if (data.containsKey("make")) {
            this.make = (String) data.get("make");
        }
        if (data.containsKey("model")) {
            this.model = (String) data.get("model");
        }
        if (data.containsKey("serialNumber")) {
            this.serialNumber = (String) data.get("serialNumber");
        }
        if (data.containsKey("comment")) {
            this.comment = (String) data.get("comment");
        }

        this.cost = new BigDecimal((String) data.get("cost")).setScale(2);
    }

    public HashMap<String, Object> data() {
        return new HashMap<>();
    }

    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getComment() { return comment; }
}
