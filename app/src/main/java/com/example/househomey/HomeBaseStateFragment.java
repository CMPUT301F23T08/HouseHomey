package com.example.househomey;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.example.househomey.filter.model.Filter;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.ui.DateFilterFragment;
import com.example.househomey.filter.ui.KeywordFilterFragment;
import com.example.househomey.filter.ui.MakeFilterFragment;
import com.example.househomey.filter.ui.TagFilterFragment;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HomeBaseStateFragment extends HomeFragment implements FilterCallback {
    private Set<Filter> appliedFilters = new HashSet<>();

    /**
     * This constructs a new HomeFragment with the appropriate list of items
     *
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeBaseStateFragment(CollectionReference itemRef) {
        super(itemRef);
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
                dateFilterFragment.show(requireActivity().getSupportFragmentManager(), "dates_filter_dialog");
            } else if (itemId == R.id.filter_by_make) {
                View makeFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_make, null);
                MakeFilterFragment makeFilterFragment = new MakeFilterFragment("Modify Make Filter", makeFilterView, this);
                makeFilterFragment.show(requireActivity().getSupportFragmentManager(), "make_filter_dialog");
            } else if (itemId == R.id.filter_by_keywords) {
                View keywordFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
                KeywordFilterFragment keywordFilterFragment = new KeywordFilterFragment("Modify Keyword Filter", keywordFilterView, this);
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
     * Applies the list of filters to the item list, resulting in a filtered list of items.
     * This method iterates through the applied filters, applying each filter in sequence,
     * and then updates the item adapter with the filtered list of items.
     */
    private void applyFilters() {
        ArrayList<Item> filteredList = new ArrayList<>(itemList);
        for (Filter filter : appliedFilters) {
            filteredList = filter.filterList(filteredList);
        }
        itemAdapter.clear();
        itemAdapter.addAll(filteredList);
        itemAdapter.notifyDataSetChanged();
    }
}
