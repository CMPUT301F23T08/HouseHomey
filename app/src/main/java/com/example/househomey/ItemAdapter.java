package com.example.househomey;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A child of ArrayAdapter this adapter specifically displays a list of class Item objects
 * @author Lukas Bonkowski, Matthew Neufeld
 * @see Item
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private Context context;

    /**
     * Constructs a new ItemAdapter with an ArrayList of items
     * @param context The context containing this adapter
     * @param items ArrayList of items to display
     */
    public ItemAdapter(Context context, ArrayList<Item> items){
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }


    /**
     * Gets a view to display an items from this adapters ArrayList.
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return The view with all data from the items displayed
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // Inflate new view if there is nothing to recycle
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        // Get the correct item
        Item item = items.get(position);

        // Set all the text views to their appropriate values
        ((TextView) view.findViewById(R.id.item_description_text)).setText(item.getDescription());

        String dateCost = new SimpleDateFormat("yyyy-MM-dd").format(item.getAcquisitionDate()) + " | $" + item.getCost();
        ((TextView) view.findViewById(R.id.item_text)).setText(dateCost);

        // Initialize button for viewing details of the item
        Button viewItemButton = view.findViewById(R.id.action_view);

        // Initialize fragment manager
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // When view item button is clicked, bundle item details and send them as arguments to ViewItemFragment
        viewItemButton.setOnClickListener(v -> {
            ViewItemFragment viewItemFragment = new ViewItemFragment();
            Bundle details = new Bundle();
            details.putString("make", item.getMake());
            details.putString("model", item.getModel());
            details.putString("serialNumber", item.getSerialNumber());
            details.putString("cost", item.getCost().toString());
            details.putString("comment", item.getComment());
            viewItemFragment.setArguments(details);
            // Begin a fragment transaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace the current fragment with the ViewItemFragment
            transaction.replace(R.id.fragmentContainer, viewItemFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
