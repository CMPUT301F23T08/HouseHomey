package com.example.househomey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        // Get the correct expense
        Item item = items.get(position);

        // Set all the text views to their appropriate values
        ((TextView) view.findViewById(R.id.item_description_text)).setText(item.getDescription());

        String dateCost = new SimpleDateFormat("yyyy-MM-dd").format(item.getAcquisitionDate()) + " | $" + item.getCost();
        ((TextView) view.findViewById(R.id.item_text)).setText(dateCost);

        return view;
    }
}
