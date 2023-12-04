package com.example.househomey.item;

import static com.example.househomey.utils.FragmentUtils.formatDate;
import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.tags.Tag;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Set;

/**
 * A child of ArrayAdapter this adapter specifically displays a list of class Item objects
 * @author Lukas Bonkowski, Matthew Neufeld, Sami Jagirdar
 * @see Item
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private Context context;
    private boolean selectState;

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
     * Getter for whether items are in select state or not
     * @return true or false indicating if itemAdapter is in select state or not
     */
    public boolean isSelectState() {
        return selectState;
    }

    /**
     * Setter for the select state of the item adapter
     * @param selectState boolean value indicating if the items are in select state or not
     */
    public void setSelectState(boolean selectState) {
        this.selectState = selectState;
        notifyDataSetChanged();
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
    @SuppressLint("SetTextI18n")
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
        ((TextView) view.findViewById(R.id.item_date_text)).setText(formatDate(item.getAcquisitionDate()));
        ((TextView) view.findViewById(R.id.item_cost_text)).setText("$" + item.getCost());

        ((TextView) view.findViewById(R.id.item_tag)).setVisibility(View.GONE);
        ((View) view.findViewById(R.id.make_tag_divider)).setVisibility(View.GONE);

        String makeString = item.getMake();
        Set<Tag> itemTags = item.getTags();
        ((TextView) view.findViewById(R.id.item_extra_tags_text)).setText("");
        if (itemTags.size() > 0) {
            if (makeString != "") ((View) view.findViewById(R.id.make_tag_divider)).setVisibility(View.VISIBLE);;
            ((TextView) view.findViewById(R.id.item_tag)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.item_tag)).setText(itemTags.iterator().next().getTagLabel());
            if (itemTags.size() > 1)
                ((TextView) view.findViewById(R.id.item_extra_tags_text)).setText(" +" + (itemTags.size()-1));
        }
        ((TextView) view.findViewById(R.id.item_make_text)).setText(makeString);

        // Make checkboxes visible based on whether or not we are in select state
        CheckBox itemCheckBox = view.findViewById(R.id.item_checkBox);
        if (this.isSelectState()) {
            itemCheckBox.setChecked(items.get(position).getChecked());
            itemCheckBox.setOnClickListener((buttonView) ->
                    items.get(position).setChecked(!items.get(position).getChecked()));
        }
        itemCheckBox.setVisibility(this.isSelectState() ? View.VISIBLE : View.GONE);

        // When view item button clicked, pass Item to ViewItemFragment via bundle
        view.setOnClickListener(v -> {
            if (v != itemCheckBox) {
                ViewItemFragment viewItemFragment = new ViewItemFragment();
                Bundle args = new Bundle();
                args.putSerializable("item", item);
                viewItemFragment.setArguments(args);
                navigateToFragmentPage(context, viewItemFragment);
            }
        });

        return view;
    }
}