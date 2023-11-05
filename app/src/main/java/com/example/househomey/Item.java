package com.example.househomey;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an inventory item with a variety of properties
 * @author Lukas Bonkowski, Matthew Neufeld
 * @see ItemAdapter
 */
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

    /**
     * Getter for make
     * @return make of the item
     */
    public String getMake() { return make; }

    /**
     * Getter for model
     * @return model of the item
     */
    public String getModel() { return model; }

    /**
     * Getter for serial number
     * @return serial number of the item
     */
    public String getSerialNumber() { return serialNumber; }

    /**
     * Getter for comment
     * @return comment for the item
     */
    public String getComment() { return comment; }
}
