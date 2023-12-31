package com.example.househomey.home;

import static com.example.househomey.utils.FragmentUtils.deletePhotosFromCloud;
import static com.example.househomey.utils.FragmentUtils.goBack;
import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;

import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.item.Item;
import com.example.househomey.item.ItemAdapter;
import com.example.househomey.tags.Tag;
import com.example.househomey.tags.ApplyTagFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This fragment is a child of the home fragment containing the list of the user's inventory
 * This fragment represents a state where items are selectable
 * @see HomeFragment
 * @author Sami Jagirdar
 */
public class SelectFragment extends Fragment implements DeleteItemsFragment.DeleteCallBack{
    private CollectionReference itemRef;
    private ListView itemListView;
    private Comparator<Item> currentSort;
    private String currentSortName;
    private boolean sortOrder;
    private ArrayList<Item> itemList;
    private ItemAdapter itemAdapter;
    private CollectionReference tagRef;
    private Map<String, Item> itemIdMap = new HashMap<>();
    private ListenerRegistration tagListener;

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
        this.itemRef = ((MainActivity) requireActivity()).getItemRef();
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.findViewById(R.id.base_toolbar).setVisibility(View.GONE);
        rootView.findViewById(R.id.select_toolbar).setVisibility(View.VISIBLE);

        //Populate the item list from bundle
        Bundle args = getArguments();
        if (args!=null){
            itemList = BundleCompat.getParcelableArrayList(args, "itemList", Item.class);
            createItemIdMap();
            sortOrder = args.getBoolean("sortOrder");
            currentSortName = args.getString("currentSortName");
            ((TextView) rootView.findViewById(R.id.total_value_text)).setText("$" + args.getString("listSum"));
            ((TextView) rootView.findViewById(R.id.total_count_text)).setText(Integer.toString(args.getInt("listCount")));
        }
        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), itemList);
        itemListView.setAdapter(itemAdapter);
        itemAdapter.setSelectState(true);

        tagRef = ((MainActivity) requireActivity()).getTagRef();
        tagListener = tagRef.addSnapshotListener(this::setupTagListener);

        final Button cancelButton = rootView.findViewById(R.id.cancel_select_button);
        cancelButton.setOnClickListener(v -> {
            unselectAllItems();
            goBack(getContext());
        });

        final Button deleteButton = rootView.findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(v -> {
            ArrayList<Item> selectedItems = getSelectedItems();
            if (selectedItems.size() > 0) {
                DeleteItemsFragment fragment = new DeleteItemsFragment(this, selectedItems);
                fragment.show(requireActivity().getSupportFragmentManager(),"Delete Items");
            }
            else {
                Toast.makeText(requireActivity().getApplicationContext(),
                        "Please select one or more items to delete.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final Button actionTagsButton = rootView.findViewById(R.id.action_tags);
        actionTagsButton.setOnClickListener(v -> {
            ApplyTagFragment applyTagFragment = new ApplyTagFragment();

            ArrayList<Item> selectedItems = getSelectedItems();
            Bundle tagArgs = new Bundle();
            tagArgs.putParcelableArrayList("itemList", selectedItems);
            applyTagFragment.setArguments(tagArgs);
            applyTagFragment.show(requireActivity().getSupportFragmentManager(),"tagDialog");
        });

        return rootView;
    }

    /**
     * Setter for itemIdMap where it populates the Hashmap, by mapping
     * each of the items' ID to the actual item obects from our itemList
     */
    private void createItemIdMap() {
        for (Item item : itemList) {
            itemIdMap.put(item.getId(), item);
        }
    }

    /**
     * This method updates the tags with changes in the firestore database and creates new
     * tag objects
     * @param querySnapshots The updated information on the inventory from the database
     * @param error Non-null if an error occurred in Firestore
     */
    private void setupTagListener(QuerySnapshot querySnapshots, FirebaseFirestoreException error) {
        if (error != null) {
            Log.e("Firestore", error.toString());
            return;
        }
        if (querySnapshots != null) {
            itemList.forEach(Item::clearTags);
            for (QueryDocumentSnapshot doc: querySnapshots) {
                Tag tag = new Tag(doc.getId(), doc.getData());
                for (String id : tag.getItemIds()) {
                    Item item = itemIdMap.get(id);
                    if (item != null) item.addTag(tag);
                }
            }
            itemAdapter.notifyDataSetChanged();
        }
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
            deletePhotosFromCloud(requireActivity(), item.getPhotoIds());
            batch.delete(itemRef.document(item.getId()));
        }
        batch.commit()
                .addOnSuccessListener((result) -> {
                    Log.i("Firestore", "Items successfully deleted");
                })
                .addOnFailureListener((error) -> {
                    Log.e("Firestore", "Failed to remove items.", error);
                });

        goBack(getContext());
      
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
        return "Delete "+selectedItems.size()+" item(s)?";
    }

    /**
     * This method checks for all the selected items in the list and returns them
     * @return List of selected items
     */
    public ArrayList<Item> getSelectedItems() {
        return itemList.stream()
                .filter(Item::getChecked)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * This method unselects all the items in the list displayed and unchecks the corresponding
     * checkboxes as well.
     * @see Item
     * @see ItemAdapter
     */
    private void unselectAllItems() {

        for (int i=0;i<itemList.size();i++) {
            // uncheck selected checkboxes
            View itemView = itemListView.getChildAt(i);
            if (itemView!=null) {
                CheckBox itemCheckBox = itemView.findViewById(R.id.item_checkBox);
                itemCheckBox.setChecked(false);
                itemList.get(i).setChecked(false);
            }
        }
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Remove the tag listener when the fragment is stopped
     */
    @Override
    public void onStop() {
        super.onStop();
        tagListener.remove();
    }
}
