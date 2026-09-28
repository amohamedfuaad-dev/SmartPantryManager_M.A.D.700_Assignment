package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;
import com.richfield.smartpantry.util.AppExecutors;

import java.util.List;
import java.util.Locale;

/**
 * Displays a recipe and clearly identifies required vs optional ingredients.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtIngredients;
    private TextView txtMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        txtName = findViewById(R.id.txtRecipeName);
        txtDescription = findViewById(R.id.txtRecipeDescription);
        txtIngredients = findViewById(R.id.txtRecipeIngredients);
        txtMethod = findViewById(R.id.txtRecipeMethod);

        long recipeId = getIntent().getLongExtra("recipe_id", -1);

        if (recipeId == -1) {
            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    private void loadRecipe(long recipeId) {
        AppExecutors.database(() -> {
            Recipe recipe = databaseHelper.getRecipe(recipeId);

            if (recipe == null) {
                AppExecutors.main(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        finish();
                    }
                });
                return;
            }

            List<RecipeIngredient> ingredients =
                    databaseHelper.getRecipeIngredients(recipeId);

            StringBuilder builder = new StringBuilder();
            builder.append("Ingredients\n\n");

            for (RecipeIngredient ingredient : ingredients) {
                builder.append("• ")
                        .append(ingredient.getName())
                        .append(" - ")
                        .append(String.format(
                                Locale.getDefault(),
                                "%.2f %s",
                                ingredient.getQuantity(),
                                ingredient.getUnit()
                        ));

                if (ingredient.isOptional()) {
                    builder.append(" (OPTIONAL)");
                } else {
                    builder.append(" (REQUIRED)");
                }

                builder.append("\n");
            }

            builder.append("\nOptional ingredients can be missing and the recipe can still match.");

            AppExecutors.main(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                txtName.setText(recipe.getName());
                txtDescription.setText(recipe.getDescription());
                txtIngredients.setText(builder.toString());
                txtMethod.setText("Preparation Method\n\n" + recipe.getMethod());
            });
        });
    }
}
