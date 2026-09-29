package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.adapter.RecipeAdapter;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays recipes that pass the application's strict
 * pantry ingredient matching algorithm.
 *
 * Matching is handled by DatabaseHelper. This activity is
 * responsible for loading the matching recipes and displaying
 * them to the user.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecipeAdapter adapter;
    private final List<Recipe> recipes = new ArrayList<>();
    private TextView txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        // Initialise the database helper and information message.
        databaseHelper = new DatabaseHelper(this);
        txtInfo = findViewById(R.id.txtSuggestionInfo);

        // Set up the RecyclerView used to display matching recipes.
        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Connect recipe selections to the recipe detail screen.
        adapter = new RecipeAdapter(recipes, recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
    }

    /**
     * Reloads recipe suggestions whenever the screen becomes
     * visible. This ensures suggestions reflect the current
     * pantry contents.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /**
     * Loads recipes that satisfy the strict matching rules.
     *
     * The database query runs in the background and the
     * RecyclerView is updated on the Android main thread.
     */
    private void loadSuggestions() {
        AppExecutors.database(() -> {
            List<Recipe> matches = databaseHelper.getStrictSuggestions();

            AppExecutors.main(() -> {
                // Stop if the activity is no longer available.
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                // Replace the displayed recipes with the latest matches.
                recipes.clear();
                recipes.addAll(matches);
                adapter.notifyDataSetChanged();

                // Show appropriate feedback when no recipes match.
                if (recipes.isEmpty()) {
                    txtInfo.setText(
                            "No recipes currently match. " +
                                    "Every required ingredient must be available in sufficient quantity " +
                                    "and the unit must match. Optional ingredients are ignored."
                    );
                } else {
                    txtInfo.setText(
                            recipes.size() + " recipe(s) match your pantry. " +
                                    "Optional ingredients are allowed to be missing."
                    );
                }
            });
        });
    }
}