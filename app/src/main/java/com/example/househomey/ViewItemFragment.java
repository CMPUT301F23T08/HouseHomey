package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static java.util.Optional.ofNullable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.househomey.form.EditItemFragment;

import java.util.Collection;

/**
 * This fragment is for the "View Item Page" - which currently displays the details and comment linked
 * to the item
 * @author Matthew Neufeld
 */
public class ViewItemFragment extends Fragment {
    private Item item;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view item fragment
     */
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_item, container, false);

        // Initialize TextViews
        TextView make = rootView.findViewById(R.id.view_item_make);
        TextView model = rootView.findViewById(R.id.view_item_model);
        TextView serialNumber = rootView.findViewById(R.id.view_item_serial_number);
        TextView cost = rootView.findViewById(R.id.view_item_cost);
        //TextView tags = rootView.findViewById(R.id.view_item_tags);
        TextView comment = rootView.findViewById(R.id.view_item_comment);

        // Set TextViews to Item details sent over from ItemAdapter
        ofNullable(getArguments())
                .map(args -> args.getSerializable("item", Item.class))
                .ifPresent(item -> {
                    this.item = item;
                    make.setText(item.getMake());
                    model.setText(item.getModel());
                    serialNumber.setText(item.getSerialNumber());
                    cost.setText(item.getCost().toString());
                    //tags.setText(item.getTags().toString());
                    comment.setText(item.getComment());
                });

        // On edit button click, pass item to EditItemFragment
        rootView.findViewById(R.id.edit_button).setOnClickListener(v ->
                navigateToFragmentPage(getContext(), new EditItemFragment(item))
        );

        rootView.findViewById(R.id.delete_button).setOnClickListener(v ->
                {
                    ((MainActivity) requireActivity()).getItemRef().document(item.getId()).delete();
                    navigateToFragmentPage(getContext(), new HomeFragment());
                }
        );

        return rootView;
    }
}
