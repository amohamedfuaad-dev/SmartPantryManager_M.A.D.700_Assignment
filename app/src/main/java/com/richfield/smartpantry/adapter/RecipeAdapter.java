package com.richfield.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.Recipe;

import java.util.List;

/**
 * RecyclerView adapter used to display recipe suggestions.
 *
 * The adapter connects each Recipe object to the recipe item layout.
 * It displays the recipe name and description and provides a button
 * that allows the user to open the selected recipe.
 *
 * The adapter is responsible for displaying recipe data only.
 * Recipe matching and database operations are handled elsewhere
 * in the application.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /**
     * Listener used to notify the Suggestions screen when the user
     * selects a recipe.
     */
    public interface Listener {
        void onRecipeSelected(Recipe recipe);
    }

    // Recipes currently displayed by the RecyclerView.
    private final List<Recipe> recipes;

    // Callback used when the user selects a recipe.
    private final Listener listener;

    /**
     * Creates the adapter using the available recipes and listener.
     *
     * @param recipes recipes to display
     * @param listener listener for recipe selection
     */
    public RecipeAdapter(List<Recipe> recipes, Listener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    /**
     * Creates a ViewHolder for one recipe item.
     */
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    /**
     * Places the recipe information into the recipe item row.
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.description.setText(recipe.getDescription());
        holder.viewRecipe.setOnClickListener(v -> listener.onRecipeSelected(recipe));
    }

    /**
     * Returns the number of recipes currently displayed.
     */
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Holds references to the views used for one recipe item.
     *
     * The ViewHolder allows the RecyclerView to reuse item views
     * efficiently while the user scrolls through the recipe list.
     */
    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;
        Button viewRecipe;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtRecipeItemName);
            description = itemView.findViewById(R.id.txtRecipeItemDescription);
            viewRecipe = itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}