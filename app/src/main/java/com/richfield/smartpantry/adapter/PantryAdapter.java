package com.richfield.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.PantryItem;

import java.util.List;

/**
 * RecyclerView adapter used to display pantry items.
 *
 * The adapter connects each PantryItem object to the corresponding
 * row in the pantry RecyclerView. It also connects the Edit and
 * Delete buttons to the listener supplied by the Pantry screen.
 *
 * The adapter is responsible for displaying the data only.
 * Database operations remain outside this class.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /**
     * Listener used to notify the Pantry screen when the user
     * selects Edit or Delete for a pantry item.
     */
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    // Current pantry items displayed by the RecyclerView.
    private final List<PantryItem> items;

    // Callback used for Edit and Delete actions.
    private final Listener listener;

    /**
     * Creates the adapter using the pantry item list and action listener.
     *
     * @param items pantry items to display
     * @param listener listener for Edit and Delete actions
     */
    public PantryAdapter(List<PantryItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * Creates a ViewHolder for one pantry item row.
     */
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    /**
     * Places the pantry item's information into the row.
     */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(
                String.format("%.2f %s", item.getQuantity(), item.getUnit())
        );

        String expiry = item.getExpiryDate();
        holder.expiry.setText(
                expiry == null || expiry.trim().isEmpty()
                        ? "Expiry: Not specified"
                        : "Expiry: " + expiry
        );

        // Pass Edit and Delete actions back to the Pantry screen.
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    /**
     * Returns the number of pantry items currently displayed.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Holds references to the views used for one pantry item.
     *
     * Using a ViewHolder avoids repeatedly looking up the same
     * views while the RecyclerView is scrolling.
     */
    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView name, quantity, expiry;
        Button edit, delete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtItemName);
            quantity = itemView.findViewById(R.id.txtItemQuantity);
            expiry = itemView.findViewById(R.id.txtItemExpiry);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}