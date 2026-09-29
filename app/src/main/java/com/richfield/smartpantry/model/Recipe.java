package com.richfield.smartpantry.model;

/**
 * Model class representing a recipe stored in the SQLite database.
 *
 * This class holds the basic information displayed for a recipe:
 * the recipe ID, name, description and preparation method.
 *
 * Database operations are handled by DatabaseHelper.
 */
public class Recipe {

    // Unique ID assigned to the recipe in the database.
    private long id;

    // Recipe name displayed to the user.
    private String name;

    // Short description of the recipe.
    private String description;

    // Preparation instructions for the recipe.
    private String method;

    /**
     * Empty constructor used when a Recipe object needs to be
     * created before its values are assigned.
     */
    public Recipe() {
    }

    /**
     * Creates a Recipe object with its stored database values.
     *
     * @param id recipe database ID
     * @param name recipe name
     * @param description recipe description
     * @param method preparation instructions
     */
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