package com.richfield.smartpantry.model;

/**
 * Model class representing one ingredient stored in the user's pantry.
 *
 * A pantry item contains the information needed to display and manage
 * an ingredient, including its name, quantity, unit and optional
 * expiry date.
 *
 * This class only stores data. Database operations are handled by
 * DatabaseHelper.
 */
public class PantryItem {

    // Unique database ID for this pantry item.
    private long id;

    // Name of the ingredient stored in the pantry.
    private String name;

    // Quantity of the ingredient currently available.
    private double quantity;

    // Unit used for the stored quantity, such as g, kg, ml or units.
    private String unit;

    // Optional expiry date entered for the pantry item.
    private String expiryDate;

    /**
     * Empty constructor used when creating a PantryItem object
     * before assigning its values.
     */
    public PantryItem() {
    }

    /**
     * Creates a PantryItem using an existing database ID.
     *
     * @param id database ID
     * @param name ingredient name
     * @param quantity available quantity
     * @param unit quantity unit
     * @param expiryDate optional expiry date
     */
    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    /**
     * Creates a new PantryItem before it has been assigned
     * a database ID.
     *
     * @param name ingredient name
     * @param quantity available quantity
     * @param unit quantity unit
     * @param expiryDate optional expiry date
     */
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this(0, name, quantity, unit, expiryDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}