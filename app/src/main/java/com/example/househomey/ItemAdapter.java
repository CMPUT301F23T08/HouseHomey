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

public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private Context context;

    public ItemAdapter(Context context, ArrayList<Item> items){
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

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

        Button viewItemButton = view.findViewById(R.id.action_view);
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
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
