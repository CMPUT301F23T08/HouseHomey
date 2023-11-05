package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This fragment represents the home screen containing the primary list of the user's inventory
 * @author Owen Cooke, Lukas Bonkowski
 */
public class HomeFragment extends Fragment {
    private CollectionReference itemRef;
    private ListView itemListView;

    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayAdapter<Item> itemAdapter;

    /**
     * This constructs a new HomeFragment with the appropriate list of items
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return the home fragment view containing the inventory list
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), itemList);
        itemListView.setAdapter(itemAdapter);

        itemRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                itemList.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    Map<String, Object> data = new HashMap<>();
                    data.putAll(doc.getData());
                    itemList.add(new Item(doc.getId(), data));
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }
}
