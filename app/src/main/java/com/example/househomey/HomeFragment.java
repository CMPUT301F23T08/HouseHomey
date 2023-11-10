package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.filter.model.DateFilter;
import com.example.househomey.filter.model.KeywordFilter;
import com.example.househomey.filter.model.MakeFilter;
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

import java.util.Date;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.ArrayList;
import java.util.Set;

/**
 * This fragment represents the home screen containing the primary list of the user's inventory
 * @author Owen Cooke, Jared Drueco, Lukas Bonkowski
 */
public class HomeFragment extends Fragment implements FilterCallback {
    private CollectionReference itemRef;
    private ListView itemListView;
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<Item> filteredItemList = new ArrayList<>();
    private Set<Filter> appliedFilters = new HashSet<>();
    private ArrayAdapter<Item> itemAdapter;
    private TextView listCountView;
    private TextView listSumView;

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
        this.itemRef = ((MainActivity) requireActivity()).getItemRef();
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        listCountView = rootView.findViewById(R.id.total_count_text);
        listSumView = rootView.findViewById(R.id.total_value_text);

        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), filteredItemList);
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
            }
            applyFilters();
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
                DateFilterFragment dateFilterFragment = new DateFilterFragment("Modify Date Filter", dateFilterView, this);
                for (Filter filter : appliedFilters) {
                    if (filter instanceof DateFilter) {
                        DateFilter dateFilter = (DateFilter) filter;
                        dateFilterFragment = new DateFilterFragment("Modify Make Filter", dateFilterView, this, dateFilter);
                    }
                }
                dateFilterFragment.show(requireActivity().getSupportFragmentManager(), "dates_filter_dialog");
            } else if (itemId == R.id.filter_by_make) {
                View makeFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_make, null);
                MakeFilterFragment makeFilterFragment = new MakeFilterFragment("Modify Make Filter", makeFilterView, this);
                for (Filter filter : appliedFilters) {
                    if (filter instanceof MakeFilter) {
                        MakeFilter makeFilter = (MakeFilter) filter;
                        makeFilterFragment = new MakeFilterFragment("Modify Make Filter", makeFilterView, this, makeFilter);
                    }
                }
                makeFilterFragment.show(requireActivity().getSupportFragmentManager(), "make_filter_dialog");
            } else if (itemId == R.id.filter_by_keywords) {
                View keywordFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
                KeywordFilterFragment keywordFilterFragment = new KeywordFilterFragment("Modify Keyword Filter", keywordFilterView, this);
                for (Filter filter : appliedFilters) {
                    if (filter instanceof KeywordFilter) {
                        KeywordFilter myFilter = (KeywordFilter) filter;
                        keywordFilterFragment = new KeywordFilterFragment("Modify Keyword Filter", keywordFilterView, this, myFilter);
                    }
                }
                keywordFilterFragment.show(requireActivity().getSupportFragmentManager(), "keywords_filter_dialog");
            } else if (itemId == R.id.filter_by_tags) {
                View tagFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_tags, null);
                TagFilterFragment tagFilterFragment = new TagFilterFragment("Modify Tag Filter", tagFilterView, this);
                tagFilterFragment.show(requireActivity().getSupportFragmentManager(), "tags_filter_dialog");
            } else {
                return false;
            }
            return true;
        });

        popupMenu.show();
    }

    /**
     * Called when a filter is applied to the item list. This method adds the filter
     * to the list of applied filters and triggers the filtering process. If the filter
     * is already applied, it is removed and added again to re-apply with the new filter value.
     *
     * @param filter The filter to be applied to the item list.
     */
    @Override
    public void onFilterApplied(Filter filter) {
        if (!appliedFilters.add(filter)) {
            appliedFilters.remove(filter);
            appliedFilters.add(filter);
        }
        applyFilters();
    }

    /**
     * Resets filters of a specific class by removing all instances of a filter from the applied
     * filters list and then re-applies the remaining filters.
     *
     * @param filter The filter to reset.
     */
    public void onFilterReset(Filter filter) {
        appliedFilters.remove(filter);
        applyFilters();
    }

    /**
     * Applies the list of filters to the item list, resulting in a filtered list of items.
     * This method iterates through the applied filters, applying each filter in sequence,
     * and then updates the item adapter with the filtered list of items.
     */
    private void applyFilters() {
        ArrayList<Item> tempList = itemList;
        for (Filter filter : appliedFilters) {
            tempList = filter.filterList(tempList);
        }
        filteredItemList.clear();
        filteredItemList.addAll(tempList);
        itemAdapter.notifyDataSetChanged();
        updateListData();
    }

    /**
     * Updates the displays above list containing information on Total Value and No. of Items
     * in the List. It works using the itemList array so it should be called whenever the
     * list is modified.
     */
    private void updateListData() {
        BigDecimal listSum = new BigDecimal(0.00);
        int listCount = filteredItemList.size();
        for (int i = 0; i < listCount; i++) {
            listSum = listSum.add(filteredItemList.get(i).getCost());
        }
        this.listSumView.setText("$" + listSum.toString());
        this.listCountView.setText(Integer.toString(listCount));
    }
}