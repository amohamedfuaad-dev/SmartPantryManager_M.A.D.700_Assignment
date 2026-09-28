package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.adapter.PantryAdapter;
import com.richfield.smartpantry.data.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays pantry ingredients and supports CRUD operations.
 */
public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private PantryAdapter adapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();
    private final List<PantryItem> allPantryItems = new ArrayList<>();
    private TextView txtPantryCount;
    private EditText edtSearchPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);
        txtPantryCount = findViewById(R.id.txtPantryCount);
        edtSearchPantry = findViewById(R.id.edtSearchPantry);

        edtSearchPantry.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterPantry(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(pantryItems, new PantryAdapter.Listener() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(PantryActivity.this, AddEditIngredientActivity.class);
                intent.putExtra("ingredient_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                confirmDelete(item);
            }
        });

        recycler.setAdapter(adapter);

        Button add = findViewById(R.id.btnAddIngredient);
        add.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        Button suggestions = findViewById(R.id.btnPantrySuggestions);
        suggestions.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        AppExecutors.database(() -> {
            List<PantryItem> loadedItems = databaseHelper.getAllPantryItems();

            AppExecutors.main(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                allPantryItems.clear();
                allPantryItems.addAll(loadedItems);
                filterPantry(edtSearchPantry.getText().toString());
            });
        });
    }

    private void filterPantry(String query) {
        String search = query == null ? "" : query.trim().toLowerCase();
        pantryItems.clear();
        for (PantryItem item : allPantryItems) {
            if (search.isEmpty() || item.getName().toLowerCase().contains(search)) {
                pantryItems.add(item);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        txtPantryCount.setText("Showing " + pantryItems.size() + " of " + allPantryItems.size() + " ingredients");
    }

    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Delete " + item.getName() + " from the pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    AppExecutors.database(() -> {
                        databaseHelper.deletePantryItem(item.getId());

                        AppExecutors.main(() -> {
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            loadPantry();
                            Toast.makeText(
                                    this,
                                    "Ingredient deleted.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    });
                })
                .show();
    }
}
