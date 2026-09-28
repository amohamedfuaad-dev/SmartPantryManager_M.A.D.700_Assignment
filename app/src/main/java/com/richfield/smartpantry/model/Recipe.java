package com.richfield.smartpantry.model;

/**
 * Model representing a recipe stored in SQLite.
 */
public class Recipe {

    private long id;
    private String name;
    private String description;
    private String method;

    public Recipe() {
    }

    public Recipe(long id, String name, String description, String method) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.method = method;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}