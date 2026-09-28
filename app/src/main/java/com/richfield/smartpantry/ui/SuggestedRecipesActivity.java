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
 * Shows only recipes that pass the strict matching algorithm.
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

        databaseHelper = new DatabaseHelper(this);
        txtInfo = findViewById(R.id.txtSuggestionInfo);

        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeAdapter(recipes, recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        AppExecutors.database(() -> {
            List<Recipe> matches = databaseHelper.getStrictSuggestions();

            AppExecutors.main(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                recipes.clear();
                recipes.addAll(matches);
                adapter.notifyDataSetChanged();

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
