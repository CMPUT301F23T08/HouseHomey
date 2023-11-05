package com.example.househomey;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an inventory item with a variety of properties
 * @author Lukas Bonkowski
 * @see ItemAdapter
 */
public class Item {
    public String id;
    private String description;
    private Date acquisitionDate;
    private String make = "";
    private String model = "";
    private String serialNumber = "";
    private String comment = "";
    private BigDecimal cost;

    /**
     * This constructs a new item from a Map of data with a reference to it's firestore docunebt
     * @param id The id of this object's document in the firestore database
     * @param data The data from that document to initialize the instance
     */
    public Item(String id, @NonNull Map<String, Object> data) {
        // Required fields
        this.id = id;
        this.description = (String) data.get("description");
        this.acquisitionDate = ((Timestamp) data.get("acquisitionDate")).toDate();
        this.cost = new BigDecimal((String) data.get("cost")).setScale(2);

        // Optional fields
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
    }

    /**
     * Getter for acquisitionDate
     * @return The acquisition date of this item
     */
    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Getter for description
     * @return The brief description of this item
     */
    public String getDescription() {
        return description;
    }


    /**
     * Getter for cost
     * @return The cost of this item
     */
    public BigDecimal getCost() {
        return cost;
    }
}
