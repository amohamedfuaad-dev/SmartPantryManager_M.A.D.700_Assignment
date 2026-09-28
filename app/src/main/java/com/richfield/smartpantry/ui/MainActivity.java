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
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        txtSummary = findViewById(R.id.txtSummary);

        Button pantry = findViewById(R.id.btnPantry);
        Button suggestions = findViewById(R.id.btnSuggestions);
        Button settings = findViewById(R.id.btnSettings);

        pantry.setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)));

        suggestions.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        settings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppExecutors.database(() -> {
            int count = databaseHelper.getAllPantryItems().size();

            AppExecutors.main(() -> {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_pantry) {
            startActivity(new Intent(this, PantryActivity.class));
            return true;
        }
        if (id == R.id.menu_recipes) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
