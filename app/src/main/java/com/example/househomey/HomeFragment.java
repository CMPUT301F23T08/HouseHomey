package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.filter.ui.DateFilterFragment;
import com.example.househomey.filter.model.Filter;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.ui.KeywordFilterFragment;
import com.example.househomey.filter.ui.MakeFilterFragment;
import com.example.househomey.filter.ui.TagFilterFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.ArrayList;
import java.util.Set;

/**
 * This fragment represents the home screen containing the primary list of the user's inventory
 * It contains 2 children fragments based on the state of the page
 * @see HomeSelectStateFragment
 * @see HomeBaseStateFragment
 * @author Owen Cooke, Jared Drueco, Lukas Bonkowski, Sami Jagirdar
 */
public class HomeFragment extends Fragment {
    protected CollectionReference itemRef;
    protected ListView itemListView;
    protected ArrayList<Item> itemList = new ArrayList<>();
    protected ArrayAdapter<Item> itemAdapter;

    /**
     * This constructs a new HomeFragment with the appropriate list of items
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
        itemList = new ArrayList<>();
    }

    /**
     * This method updates the itemAdapter with changes in the firestore database and creates new
     * item objects
     * @param querySnapshots The updated information on the inventory from the database
     * @param error Non-null if an error occurred in Firestore
     */
    protected void setupItemListener(QuerySnapshot querySnapshots, FirebaseFirestoreException error) {
        if (error != null) {
            Log.e("Firestore", error.toString());
            return;
        }
        if (querySnapshots != null) {
            itemList.clear();
            for (QueryDocumentSnapshot doc: querySnapshots) {
                Map<String, Object> data = new HashMap<>(doc.getData());
                itemList.add(new Item(doc.getId(), data));
                unselectAllItems();
            }
        }
    }

    /**
     * This method unselects all the items in the list displayed and unchecks the corresponding
     * checkboxes as well.
     * @see Item
     * @see ItemAdapter
     */
    protected void unselectAllItems() {

        for (int i=0;i<itemList.size();i++) {

            // unselect items
            itemList.get(i).setSelected(false);

            // uncheck selected checkboxes
            View itemView = itemListView.getChildAt(i);
            if (itemView!=null) {
                CheckBox itemCheckBox = itemView.findViewById(R.id.item_checkBox);
                itemCheckBox.setChecked(false);
            }
        }
        itemAdapter.notifyDataSetChanged();
    }

}