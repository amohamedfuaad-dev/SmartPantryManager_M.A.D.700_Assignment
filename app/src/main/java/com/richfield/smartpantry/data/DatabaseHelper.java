package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** SQLite database helper for Smart Pantry Manager. */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "method TEXT)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "optional INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE recipe_ingredients " +
                    "ADD COLUMN optional INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE recipes ADD COLUMN method TEXT");
        }
        if (oldVersion < 4) {
            seedRecipeMethods(db);
        }
    }

    // ========================= PANTRY CRUD =========================

    public long insertPantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("name", normalise(item.getName()));
        values.put("quantity", item.getQuantity());
        values.put("unit", normalise(item.getUnit()));
        values.put("expiry_date", item.getExpiryDate());
        return getWritableDatabase().insert("pantry_items", null, values);
    }

    public int updatePantryItem(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("name", normalise(item.getName()));
        values.put("quantity", item.getQuantity());
        values.put("unit", normalise(item.getUnit()));
        values.put("expiry_date", item.getExpiryDate());
        return getWritableDatabase().update("pantry_items", values, "id = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        getWritableDatabase().delete("pantry_items", null, null);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("pantry_items", null, null, null,
                null, null, "name COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                result.add(new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public PantryItem getPantryItem(long id) {
        Cursor cursor = getReadableDatabase().query("pantry_items", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    // ========================= RECIPES =========================

    public Recipe getRecipe(long recipeId) {
        Cursor cursor = getReadableDatabase().query("recipes", null, "id = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("method")));
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private List<Recipe> getAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("recipes", null, null, null,
                null, null, "name COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                result.add(new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("method"))));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("recipe_ingredients", null,
                "recipe_id = ?", new String[]{String.valueOf(recipeId)}, null, null,
                "optional ASC, name COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                result.add(new RecipeIngredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("recipe_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("optional")) == 1));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    /**
     * Returns recipes only when every required ingredient
     * is available in sufficient quantity.
     *
     * Duplicate pantry records of the same ingredient
     * are combined when calculating the available quantity.
     */
    public List<Recipe> getStrictSuggestions() {
        List<Recipe> matches = new ArrayList<>();
        List<PantryItem> pantry = getAllPantryItems();

        for (Recipe recipe : getAllRecipes()) {
            boolean matchesAllRequired = true;

            for (RecipeIngredient ingredient : getRecipeIngredients(recipe.getId())) {
                // Optional ingredients do not affect strict matching.
                if (ingredient.isOptional()) {
                    continue;
                }

                // Every required ingredient must have enough total quantity in the pantry.
                if (!hasSufficientPantryQuantity(pantry, ingredient)) {
                    matchesAllRequired = false;
                    break;
                }
            }

            if (matchesAllRequired) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    /**
     * Checks whether the pantry contains enough total quantity
     * of a required ingredient.
     *
     * Multiple pantry records containing the same ingredient
     * are combined before the quantity is compared.
     */
    private boolean hasSufficientPantryQuantity(
            List<PantryItem> pantry,
            RecipeIngredient required) {

        String requiredName = normaliseIngredientName(required.getName());
        String requiredUnit = normalise(required.getUnit());
        double requiredAmount = convertToBaseUnit(
                required.getQuantity(), requiredUnit);

        if (requiredAmount < 0) {
            return false;
        }

        double totalPantryAmount = 0;

        for (PantryItem item : pantry) {
            // Ingredient names must match after normalisation.
            if (!requiredName.equals(normaliseIngredientName(item.getName()))) {
                continue;
            }

            String pantryUnit = normalise(item.getUnit());

            // Do not combine incompatible units.
            if (!compatibleUnitGroup(pantryUnit, requiredUnit)) {
                continue;
            }

            double pantryAmount = convertToBaseUnit(
                    item.getQuantity(), pantryUnit);

            if (pantryAmount < 0) {
                continue;
            }

            // Add this pantry record to the total.
            totalPantryAmount += pantryAmount;

            // We already have enough.
            if (totalPantryAmount >= requiredAmount) {
                return true;
            }
        }

        return false;
    }

    // ========================= MATCHING HELPERS =========================

    private String normaliseIngredientName(String name) {
        String value = normalise(name).replaceAll("\\s+", " ");
        if (value.endsWith("ies") && value.length() > 4) {
            return value.substring(0, value.length() - 3) + "y";
        }
        if (value.endsWith("oes") && value.length() > 4) {
            return value.substring(0, value.length() - 2);
        }
        if (value.endsWith("s") && !value.endsWith("ss") && value.length() > 3) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    private boolean compatibleUnitGroup(String first, String second) {
        String a = normalise(first);
        String b = normalise(second);
        return a.equals(b) ||
                (isWeightUnit(a) && isWeightUnit(b)) ||
                (isVolumeUnit(a) && isVolumeUnit(b));
    }

    private boolean isWeightUnit(String unit) {
        return unit.equals("g") || unit.equals("kg");
    }

    private boolean isVolumeUnit(String unit) {
        return unit.equals("ml") || unit.equals("l");
    }

    private double convertToBaseUnit(double quantity, String unit) {
        String value = normalise(unit);
        if (value.equals("g")) return quantity;
        if (value.equals("kg")) return quantity * 1000.0;
        if (value.equals("ml")) return quantity;
        if (value.equals("l")) return quantity * 1000.0;
        if (value.equals("unit") || value.equals("units") ||
                value.equals("clove") || value.equals("cloves") ||
                value.equals("slice") || value.equals("slices") ||
                value.equals("leaf") || value.equals("leaves") ||
                value.equals("can") || value.equals("cans")) return quantity;
        return -1;
    }

    // ========================= RECIPE METHODS =========================

    private void seedRecipeMethods(SQLiteDatabase db) {
        updateRecipeMethod(db, "Spaghetti Bolognese",
                "Boil the spaghetti. Brown the mince and onion, add tomato, then simmer. Combine with the pasta and serve.");
        updateRecipeMethod(db, "Chicken Stir Fry",
                "Cook the chicken until done. Stir-fry the carrot and rice, then combine with the chicken and serve.");
        updateRecipeMethod(db, "Vegetable Omelette",
                "Beat the eggs. Cook the onion and tomato briefly, add the eggs, and cook until set.");
        updateRecipeMethod(db, "Pancakes",
                "Mix flour, milk, eggs and sugar into a smooth batter. Cook spoonfuls in a lightly heated pan until golden on both sides.");
        updateRecipeMethod(db, "Chicken Curry",
                "Cook the chicken and onion. Add tomato and curry powder, simmer until cooked through, then serve with rice.");
        updateRecipeMethod(db, "Tuna Sandwich",
                "Mix tuna with mayonnaise. Place on bread and add any available optional salad ingredients before serving.");
        updateRecipeMethod(db, "French Toast",
                "Whisk the eggs and milk. Dip the bread into the mixture and cook in a pan until golden on both sides.");
        updateRecipeMethod(db, "Rice and Beans",
                "Cook the rice. Warm the beans with onion and tomato, then serve the bean mixture over the rice.");
        updateRecipeMethod(db, "Tomato Pasta",
                "Cook the pasta. Simmer tomato and onion together, combine with the pasta, and add optional toppings.");
        updateRecipeMethod(db, "Chicken Rice Bowl",
                "Cook the chicken and rice. Cook the carrot, then assemble the chicken and vegetables over the rice.");
        updateRecipeMethod(db, "Beef Tacos",
                "Cook the mince with onion. Fill tortillas with the cooked mince and tomato, then add optional toppings.");
        updateRecipeMethod(db, "Grilled Cheese",
                "Place cheese between bread slices. Toast in a pan until the bread is golden and the cheese is melted.");
        updateRecipeMethod(db, "Mac and Cheese",
                "Cook the macaroni. Make a simple sauce with milk, cheese and optional butter/flour, then combine with the macaroni.");
        updateRecipeMethod(db, "Chicken Wrap",
                "Cook the chicken. Fill tortillas with chicken, lettuce and tomato, then add optional mayonnaise.");
        updateRecipeMethod(db, "Egg Fried Rice",
                "Cook the rice and scramble the eggs. Stir-fry the rice and carrot, add egg, and finish with optional soy sauce.");
        updateRecipeMethod(db, "Bean Salad",
                "Drain the beans and combine with tomato and lettuce. Add optional onion and cheese, then serve chilled.");
        updateRecipeMethod(db, "Creamy Chicken Pasta",
                "Cook the pasta and chicken. Warm the cream, combine with chicken and pasta, then add optional cheese and garlic.");
        updateRecipeMethod(db, "Breakfast Scramble",
                "Scramble the eggs until cooked. Serve with bread and any available optional tomato, onion or cheese.");
        updateRecipeMethod(db, "Beef Rice Bowl",
                "Cook the mince and onion. Serve the cooked beef over rice with optional carrot and soy sauce.");
        updateRecipeMethod(db, "Simple Tomato Soup",
                "Cook the tomato and onion until soft. Blend or mash into a soup and serve with optional milk or bread.");
    }

    private void updateRecipeMethod(SQLiteDatabase db, String recipeName, String method) {
        ContentValues values = new ContentValues();
        values.put("method", method);
        db.update("recipes", values, "name = ?", new String[]{recipeName});
    }

    // ========================= SEED DATA =========================

    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
                {"Spaghetti Bolognese", "Classic tomato and beef pasta.", "Boil the spaghetti. Brown the mince and onion, add tomato, then simmer. Combine with the pasta and serve.", "spaghetti|200|g|false", "mince|250|g|false", "tomato|2|unit|false", "onion|1|unit|false", "garlic|1|clove|true", "cheese|30|g|true"},
                {"Chicken Stir Fry", "Quick chicken and vegetable stir fry.", "Cook the chicken until done. Stir-fry the carrot and rice, then combine with the chicken and serve.", "chicken|250|g|false", "rice|200|g|false", "carrot|1|unit|false", "soy sauce|20|ml|true", "garlic|1|clove|true"},
                {"Vegetable Omelette", "Simple omelette with vegetables.", "Beat the eggs. Cook the onion and tomato briefly, add the eggs, and cook until set.", "egg|3|unit|false", "onion|1|unit|false", "tomato|1|unit|false", "cheese|30|g|true"},
                {"Pancakes", "Basic breakfast pancakes.", "Mix flour, milk, eggs and sugar into a smooth batter. Cook spoonfuls in a lightly heated pan until golden on both sides.", "flour|200|g|false", "milk|250|ml|false", "egg|2|unit|false", "sugar|20|g|false", "butter|20|g|true"},
                {"Chicken Curry", "Creamy tomato chicken curry.", "Cook the chicken and onion. Add tomato and curry powder, simmer until cooked through, then serve with rice.", "chicken|300|g|false", "rice|200|g|false", "tomato|2|unit|false", "onion|1|unit|false", "curry powder|10|g|false", "cream|100|ml|true"},
                {"Tuna Sandwich", "Easy tuna sandwich.", "Mix tuna with mayonnaise. Place on bread and add any available optional salad ingredients before serving.", "bread|2|slice|false", "tuna|1|can|false", "mayonnaise|20|g|false", "lettuce|2|leaf|true", "tomato|1|unit|true"},
                {"French Toast", "Sweet egg-dipped toast.", "Whisk the eggs and milk. Dip the bread into the mixture and cook in a pan until golden on both sides.", "bread|2|slice|false", "egg|2|unit|false", "milk|100|ml|false", "sugar|10|g|true", "cinnamon|2|g|true"},
                {"Rice and Beans", "Budget-friendly rice and beans.", "Cook the rice. Warm the beans with onion and tomato, then serve the bean mixture over the rice.", "rice|200|g|false", "beans|1|can|false", "onion|1|unit|false", "tomato|1|unit|false", "chilli|1|unit|true"},
                {"Tomato Pasta", "Simple tomato pasta.", "Cook the pasta. Simmer tomato and onion together, combine with the pasta, and add optional toppings.", "pasta|200|g|false", "tomato|2|unit|false", "onion|1|unit|false", "garlic|1|clove|true", "cheese|30|g|true"},
                {"Chicken Rice Bowl", "Chicken served over seasoned rice.", "Cook the chicken and rice. Cook the carrot, then assemble the chicken and vegetables over the rice.", "chicken|250|g|false", "rice|200|g|false", "carrot|1|unit|false", "soy sauce|15|ml|true", "egg|1|unit|true"},
                {"Beef Tacos", "Simple beef tacos.", "Cook the mince with onion. Fill tortillas with the cooked mince and tomato, then add optional toppings.", "mince|250|g|false", "tortilla|4|unit|false", "tomato|1|unit|false", "onion|1|unit|false", "cheese|40|g|true", "lettuce|2|leaf|true"},
                {"Grilled Cheese", "Crispy cheese sandwich.", "Place cheese between bread slices. Toast in a pan until the bread is golden and the cheese is melted.", "bread|2|slice|false", "cheese|60|g|false", "butter|15|g|true"},
                {"Mac and Cheese", "Creamy macaroni cheese.", "Cook the macaroni. Make a simple sauce with milk, cheese and optional butter/flour, then combine with the macaroni.", "macaroni|200|g|false", "cheese|100|g|false", "milk|200|ml|false", "butter|20|g|true", "flour|20|g|true"},
                {"Chicken Wrap", "Chicken wrap with fresh filling.", "Cook the chicken. Fill tortillas with chicken, lettuce and tomato, then add optional mayonnaise.", "chicken|200|g|false", "tortilla|2|unit|false", "lettuce|2|leaf|false", "tomato|1|unit|false", "mayonnaise|20|g|true"},
                {"Egg Fried Rice", "Fried rice with egg.", "Cook the rice and scramble the eggs. Stir-fry the rice and carrot, add egg, and finish with optional soy sauce.", "rice|200|g|false", "egg|2|unit|false", "carrot|1|unit|false", "soy sauce|15|ml|true", "onion|1|unit|true"},
                {"Bean Salad", "Fresh bean and tomato salad.", "Drain the beans and combine with tomato and lettuce. Add optional onion and cheese, then serve chilled.", "beans|1|can|false", "tomato|2|unit|false", "lettuce|2|leaf|false", "onion|1|unit|true", "cheese|30|g|true"},
                {"Creamy Chicken Pasta", "Creamy chicken pasta.", "Cook the pasta and chicken. Warm the cream, combine with chicken and pasta, then add optional cheese and garlic.", "pasta|200|g|false", "chicken|200|g|false", "cream|100|ml|false", "cheese|40|g|true", "garlic|1|clove|true"},
                {"Breakfast Scramble", "Egg scramble with bread.", "Scramble the eggs until cooked. Serve with bread and any available optional tomato, onion or cheese.", "egg|3|unit|false", "bread|2|slice|false", "tomato|1|unit|true", "onion|1|unit|true", "cheese|30|g|true"},
                {"Beef Rice Bowl", "Seasoned beef served with rice.", "Cook the mince and onion. Serve the cooked beef over rice with optional carrot and soy sauce.", "mince|250|g|false", "rice|200|g|false", "onion|1|unit|false", "carrot|1|unit|true", "soy sauce|15|ml|true"},
                {"Simple Tomato Soup", "Warm tomato soup.", "Cook the tomato and onion until soft. Blend or mash into a soup and serve with optional milk or bread.", "tomato|3|unit|false", "onion|1|unit|false", "milk|100|ml|true", "bread|2|slice|true"}
        };

        for (String[] recipe : data) {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", recipe[0]);
            recipeValues.put("description", recipe[1]);
            recipeValues.put("method", recipe[2]);

            long recipeId = db.insert("recipes", null, recipeValues);
            if (recipeId == -1) {
                continue;
            }

            for (int i = 3; i < recipe.length; i++) {
                String[] parts = recipe[i].split("\\|");
                if (parts.length != 4) {
                    continue;
                }

                ContentValues ingredient = new ContentValues();
                ingredient.put("recipe_id", recipeId);
                ingredient.put("name", normalise(parts[0]));
                ingredient.put("quantity", Double.parseDouble(parts[1]));
                ingredient.put("unit", normalise(parts[2]));
                ingredient.put("optional", Boolean.parseBoolean(parts[3]) ? 1 : 0);

                db.insert("recipe_ingredients", null, ingredient);
            }
        }
    }

    private String normalise(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}