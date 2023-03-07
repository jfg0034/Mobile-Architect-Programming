package com.example.inventoryapp;

// Item class
public class Item {

    private static final int MAX_COUNT = 9999999; // Max allowed quantity
    private long mID = 0;
    private String itemName = "";
    private int quantity = 0;

    // Setters and getters
    public void setID(long mID) {
        this.mID = mID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getID() {
        return mID;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxCount() {
        return MAX_COUNT;
    }
}
