package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.AppExecutors;

/**
 * Form used for adding and editing pantry ingredients.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private AutoCompleteTextView edtName;
    private EditText edtQuantity;
    private Spinner spinnerUnit;
    private EditText edtExpiry;
    private TextView txtTitle;

    private long ingredientId = -1;

    private final String[] units = {
            "g", "kg", "ml", "l", "unit", "clove", "slice", "leaf", "can"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        txtTitle = findViewById(R.id.txtFormTitle);
        edtName = findViewById(R.id.edtIngredientName);

        String[] everydayIngredients = {
                "Apple", "Banana", "Beans", "Beef", "Bread", "Butter", "Carrot",
                "Cheese", "Chicken", "Chilli", "Cinnamon", "Cream", "Egg", "Flour",
                "Garlic", "Lettuce", "Macaroni", "Mayonnaise", "Milk", "Mince",
                "Onion", "Pasta", "Potato", "Rice", "Salt", "Sugar", "Tomato",
                "Tuna", "Tortilla", "Soy Sauce", "Spinach", "Yoghurt", "Oil"
        };
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, everydayIngredients);
        edtName.setAdapter(ingredientAdapter);
        edtName.setThreshold(1);
        edtQuantity = findViewById(R.id.edtQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        edtExpiry = findViewById(R.id.edtExpiry);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        ingredientId = getIntent().getLongExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            txtTitle.setText("Edit Ingredient");
            loadExistingIngredient();
        }

        Button save = findViewById(R.id.btnSaveIngredient);
        Button cancel = findViewById(R.id.btnCancelIngredient);

        save.setOnClickListener(v -> saveIngredient());
        cancel.setOnClickListener(v -> finish());
    }

    private void loadExistingIngredient() {
        AppExecutors.database(() -> {
            PantryItem item = databaseHelper.getPantryItem(ingredientId);

            AppExecutors.main(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (item == null) {
                    Toast.makeText(
                            this,
                            "Ingredient not found.",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                    return;
                }

                edtName.setText(item.getName());
                edtQuantity.setText(String.valueOf(item.getQuantity()));
                edtExpiry.setText(item.getExpiryDate());

                for (int i = 0; i < units.length; i++) {
                    if (units[i].equalsIgnoreCase(item.getUnit())) {
                        spinnerUnit.setSelection(i);
                        break;
                    }
                }
            });
        });
    }

    private void saveIngredient() {
        String name = edtName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = edtExpiry.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Enter an ingredient name.");
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            edtQuantity.setError("Enter a quantity.");
            edtQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid number.");
            edtQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            edtQuantity.setError("Quantity must be greater than zero.");
            edtQuantity.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(
                ingredientId == -1 ? 0 : ingredientId,
                name,
                quantity,
                unit,
                expiry
        );

        AppExecutors.database(() -> {
            if (ingredientId == -1) {
                databaseHelper.insertPantryItem(item);
            } else {
                databaseHelper.updatePantryItem(item);
            }

            AppExecutors.main(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                Toast.makeText(
                        this,
                        ingredientId == -1 ? "Ingredient added." : "Ingredient updated.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            });
        });
    }
}
