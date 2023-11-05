package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.Filters.DateFilterFragment;
import com.example.househomey.Filters.FilterCallback;
import com.example.househomey.Filters.KeywordFilterFragment;
import com.example.househomey.Filters.MakeFilterFragment;
import com.example.househomey.Filters.TagFilterFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This fragment represents the home screen containing the primary list of the user's inventory
 * @author Owen Cooke, Jared Drueco, Lukas Bonkowski
 */
public class HomeFragment extends Fragment implements FilterCallback {
    private CollectionReference itemRef;
    private ListView itemListView;
    private Map<String, Object> appliedFilters = new HashMap<>();
    private PopupMenu filterView;

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

        itemRef.addSnapshotListener(this::setupItemListener);

        View filterButton = rootView.findViewById(R.id.filter_dropdown_button);
        filterButton.setOnClickListener(this::showFilterMenu);

        return rootView;

    }

    /**
     * This method updates the itemAdapter with changes in the firestore database and creates new
     * item objects
     * @param querySnapshots The updated information on the inventory from the database
     * @param error Non-null if an error occurred in Firestore
     */
    private void setupItemListener(QuerySnapshot querySnapshots, FirebaseFirestoreException error) {
        if (error != null) {
            Log.e("Firestore", error.toString());
            return;
        }
        if (querySnapshots != null) {
            itemList.clear();
            for (QueryDocumentSnapshot doc: querySnapshots) {
                Map<String, Object> data = new HashMap<>(doc.getData());
                itemList.add(new Item(doc.getId(), data));
                itemAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Displays the filter menu with options to select the appropriate filter
     * @param view The view to set the filter menu on
     */
    private void showFilterMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.filter, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_by_dates) {
                View dateFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_dates, null);
                DateFilterFragment dateFilterFragment = new DateFilterFragment("Modify Date Filter", dateFilterView);
                dateFilterFragment.show(requireActivity().getSupportFragmentManager(), "dates_filter_dialog");
            } else if (itemId == R.id.filter_by_make) {
                View makeFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_make, null);
                MakeFilterFragment makeFilterFragment = new MakeFilterFragment("Modify Make Filter", makeFilterView);
                makeFilterFragment.setFilterCallback(this);
                makeFilterFragment.show(requireActivity().getSupportFragmentManager(), "make_filter_dialog");
            } else if (itemId == R.id.filter_by_keywords) {
                View keywordFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
                KeywordFilterFragment keywordFilterFragment = new KeywordFilterFragment("Modify Keyword Filter", keywordFilterView);
                keywordFilterFragment.show(requireActivity().getSupportFragmentManager(), "keywords_filter_dialog");
            } else if (itemId == R.id.filter_by_tags) {
                View tagFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_tags, null);
                TagFilterFragment tagFilterFragment = new TagFilterFragment("Modify Tag Filter", tagFilterView);
                tagFilterFragment.show(requireActivity().getSupportFragmentManager(), "tags_filter_dialog");
            } else {
                return false;
            }
            return true;
        });

        popupMenu.show();
    }

    @Override
    public void onFilterApplied(String filterType, Object filterValue) {
        appliedFilters.put(filterType, filterValue);
        Toast.makeText(getContext(), filterType, Toast.LENGTH_SHORT).show();
        filterItemList();
    }

    private void filterItemList() {
        // TODO: Filter logic
        itemAdapter.notifyDataSetChanged();
    }
}
