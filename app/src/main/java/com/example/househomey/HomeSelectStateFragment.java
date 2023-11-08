package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

/**
 * This fragment is a child of the home fragment containing the list of the user's inventory
 * This fragment represents a state where items are selectable
 * @see HomeFragment
 * @author Sami Jagirdar
 */
public class HomeSelectStateFragment extends HomeFragment implements DeleteItemsFragment.DeleteCallBack{

    private ItemAdapter itemView;

    /**
     * This constructs a new HomeFragment with the appropriate list of items
     *
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeSelectStateFragment(CollectionReference itemRef) {
        super(itemRef);
    }

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
     * @return the home fragment view containing the inventory list
     * in its base state where items are selectable
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home_select, container, false);

        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), itemList);
        itemListView.setAdapter(itemAdapter);
        itemView = (ItemAdapter) itemAdapter;
        itemView.setSelectState(true);
        itemRef.addSnapshotListener(this::setupItemListener);

        final Button cancelButton = rootView.findViewById(R.id.cancel_select_button);
        cancelButton.setOnClickListener(v -> {
            unselectAllItems();
            navigateToFragmentPage(getContext(), new HomeBaseStateFragment(itemRef));
        });

        final Button deleteButton = rootView.findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(v -> {
            ArrayList<Item> selectedItems = getSelectedItems();
            if (selectedItems.size()>0) {
                DeleteItemsFragment fragment = new DeleteItemsFragment(this, selectedItems);
                fragment.show(requireActivity().getSupportFragmentManager(),"Delete Items");
            }
            else {
                Toast.makeText(requireActivity().getApplicationContext(),
                        "Please select one or more items to delete.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    /**
     * This is an interface method that handles the deletion of selected items from the user database
     * This is done upon user confirmation in the delete items dialog
     * @see DeleteItemsFragment
     * @param selectedItems Items to be deleted
     */
    @Override
    public void onOKPressed(ArrayList<Item> selectedItems){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (Item item : selectedItems) {
            batch.delete(itemRef.document(item.getId()));
        }
        batch.commit()
                .addOnSuccessListener((result) -> {
                    Log.i("Firestore", "Items successfully deleted");
                })
                .addOnFailureListener((error) -> {
                    Log.e("Firestore", "Failed to remove items.", error);
                });
    }

    /**
     * This an interface method that sets the title of the Delete items dialog
     * It asks us the user to confirm the deletion of the selected items
     * @see DeleteItemsFragment
     * @param selectedItems items selected to be deleted
     * @return string asking confirmation of deletion #selectedItems
     */
    @Override
    public String DialogTitle(ArrayList<Item> selectedItems) {
        return "Confirm deletion of "+selectedItems.size()+" item(s)?";
    }

    /**
     * This method checks for all the selected items in the list and returns them
     * @return List of selected items
     */
    public ArrayList<Item> getSelectedItems() {
        ArrayList<Item> selectedItems = new ArrayList<>();
        for (Item item : itemList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }
}
