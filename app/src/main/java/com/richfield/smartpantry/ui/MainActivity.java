package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.util.AppExecutors;

/**
 * Main dashboard screen.
 *
 * Provides navigation to the main sections of the application
 * and displays a summary of the ingredients stored in the pantry.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar for the dashboard.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialise the database and dashboard summary.
        databaseHelper = new DatabaseHelper(this);
        txtSummary = findViewById(R.id.txtSummary);

        // Get references to the main navigation buttons.
        Button pantry = findViewById(R.id.btnPantry);
        Button suggestions = findViewById(R.id.btnSuggestions);
        Button settings = findViewById(R.id.btnSettings);

        // Open the pantry screen.
        pantry.setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)));

        // Open the suggested recipes screen.
        suggestions.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        // Open the settings screen.
        settings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    /**
     * Refreshes the pantry summary whenever the dashboard
     * becomes visible again.
     *
     * Database work runs in the background and the result
     * is then displayed on the main Android UI thread.
     */
    @Override
    protected void onResume() {
        super.onResume();

        AppExecutors.database(() -> {
            int count = databaseHelper.getAllPantryItems().size();

            AppExecutors.main(() -> {
                // Do not update the screen if the activity has closed.
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                txtSummary.setText(
                        "Pantry summary\n\n" +
                                "Ingredients stored: " + count + "\n\n" +
                                "Recipe matching checks required ingredients for quantity and unit. " +
                                "Optional ingredients do not prevent a recipe from matching."
                );
            });
        });
    }

    /**
     * Adds the navigation options to the dashboard toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    /**
     * Handles navigation selections made from the toolbar menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Open the pantry screen.
        if (id == R.id.menu_pantry) {
            startActivity(new Intent(this, PantryActivity.class));
            return true;
        }

        // Open the suggested recipes screen.
        if (id == R.id.menu_recipes) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        }

        // Open the settings screen.
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}