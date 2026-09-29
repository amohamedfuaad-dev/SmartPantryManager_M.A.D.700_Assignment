package com.richfield.smartpantry.model;

/**
 * Model class representing an ingredient that belongs to a recipe.
 *
 * A recipe can contain several RecipeIngredient objects. Each object
 * stores the ingredient name, required quantity, unit and whether the
 * ingredient is optional.
 *
 * The optional value is important for recipe matching:
 * an optional ingredient does not have to be present in the pantry
 * for the recipe to qualify as a suggestion.
 *
 * This class only stores recipe ingredient data. Database operations
 * and recipe matching are handled by DatabaseHelper.
 */
public class RecipeIngredient {

    // Unique database ID for this recipe ingredient.
    private long id;

    // ID of the recipe that this ingredient belongs to.
    private long recipeId;

    // Name of the ingredient required by the recipe.
    private String name;

    // Quantity required by the recipe.
    private double quantity;

    // Unit used for the required quantity.
    private String unit;

    // Indicates whether the ingredient is optional.
    private boolean optional;

    /**
     * Empty constructor used when creating a RecipeIngredient object
     * before assigning its values.
     */
    public RecipeIngredient() {
    }

    /**
     * Creates a RecipeIngredient with its stored database values.
     *
     * @param id database ID
     * @param recipeId ID of the recipe
     * @param name ingredient name
     * @param quantity required quantity
     * @param unit quantity unit
     * @param optional whether the ingredient is optional
     */
    public RecipeIngredient(long id, long recipeId, String name,
                            double quantity, String unit, boolean optional) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.optional = optional;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}