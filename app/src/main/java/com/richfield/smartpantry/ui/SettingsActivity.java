package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.util.AppExecutors;

/**
 * Simple settings screen.
 */
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        Button clear = findViewById(R.id.btnClearPantry);

        clear.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear pantry")
                        .setMessage("This will remove all pantry ingredients. Recipes will remain.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Clear", (dialog, which) ->
                                AppExecutors.database(() -> {
                                    databaseHelper.clearPantry();

                                    AppExecutors.main(() -> {
                                        if (isFinishing() || isDestroyed()) {
                                            return;
                                        }

                                        Toast.makeText(
                                                this,
                                                "Pantry cleared.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    });
                                })
                        )
                        .show()
        );
    }
}
