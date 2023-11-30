package com.example.househomey.form;

import static com.example.househomey.utils.FragmentUtils.formatDate;
import static com.example.househomey.utils.FragmentUtils.goBack;
import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.ViewItemFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This fragment is responsible for editing an existing item in the database.
 *
 * @author Owen Cooke
 */
public class EditItemFragment extends ItemFormFragment {
    private final Item item;
    private Item updatedItem;
    private AtomicBoolean finishedItemLoad = new AtomicBoolean(false);

    /**
     * Constructs a new EditItemFragment with the item to edit.
     *
     * @param item The item to be edited
     */
    public EditItemFragment(Item item) {
        this.item = item;
    }

    /**
     * This creates the view to edit an existing item and sets the button listeners.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The fragment_edit_item view with the correct listeners added
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        // Change title and prefill inputs with the existing Item's data
        ((MaterialTextView) rootView.findViewById(R.id.add_item_title)).setText("Edit Item");
        prefillInputs(rootView);
        // Add listeners for buttons and text validation
        initDatePicker(rootView);
        initTextValidators(rootView);
        rootView.findViewById(R.id.add_item_confirm_button).setOnClickListener(v -> editItem());
        rootView.findViewById(R.id.add_item_back_button).setOnClickListener(v -> goBack(getContext()));
        return rootView;
    }

    /**
     * Creates the new Item and waits for tags to finish loading
     * @param itemId Id of the new item
     * @param data   Data for the new item
     * @return New Item with tag listener set
     */
    @Override
    protected Item createItem(String itemId, Map<String, Object> data) {
        return new Item(itemId, data, ((MainActivity) requireActivity()).getTagRef(), item -> {
            if (finishedItemLoad.getAndSet(true))
                sendItem();
        });
    }

    /**
     * Pre-fills the input fields with data from the item being edited.
     *
     * @param rootView The root view of the UI where the input fields are located.
     */
    private void prefillInputs(@NonNull View rootView) {
        if (item != null) {
            // Pre-fill the input fields with the existing item's data
            ((TextInputEditText) rootView.findViewById(R.id.add_item_description)).setText(item.getDescription());
            ((TextInputEditText) rootView.findViewById(R.id.add_item_cost)).setText(String.valueOf(item.getCost()));
            ((TextInputEditText) rootView.findViewById(R.id.add_item_make)).setText(item.getMake());
            ((TextInputEditText) rootView.findViewById(R.id.add_item_model)).setText(item.getModel());
            ((TextInputEditText) rootView.findViewById(R.id.add_item_serial_number)).setText(item.getSerialNumber());
            ((TextInputEditText) rootView.findViewById(R.id.add_item_comment)).setText(item.getComment());

            // Set the acquisition Date object and prefill with its formatted text
            dateAcquired = item.getAcquisitionDate();
            ((TextInputEditText) rootView.findViewById(R.id.add_item_date)).setText(formatDate(dateAcquired));

            // Add image URLs (if any) to the photo gallery adapter
            photoUris.addAll(item.getPhotoIds());
            photoAdapter.notifyItemRangeInserted(0, photoUris.size());
        }
    }

    /**
     * Edits an existing Item in the user's Firestore item collection.
     */
    private void editItem() {
        updatedItem = prepareItem(item.getId());
        if (updatedItem == null) return;

        // Update the existing item document in Firestore
        itemRef.document(updatedItem.getId())
                .set(updatedItem.getData())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Successfully updated item with id: " + updatedItem.getId());
                    if (finishedItemLoad.getAndSet(true))
                        sendItem();
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Failed to update item with id: " + updatedItem.getId());
                    getView().findViewById(R.id.add_item_error_msg).setVisibility(View.VISIBLE);
                });
    }

    /**
     * Sends the new item to the view item fragment
     */
    private void sendItem() {
        ViewItemFragment viewItemFragment = new ViewItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", updatedItem);
        viewItemFragment.setArguments(args);
        navigateToFragmentPage(getContext(), viewItemFragment);
    }
}
