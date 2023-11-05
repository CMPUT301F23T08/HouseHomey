package com.example.househomey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.Filters.DateFilterFragment;
import com.example.househomey.Filters.KeywordFilterFragment;
import com.example.househomey.Filters.MakeFilterFragment;
import com.example.househomey.Filters.TagFilterFragment;
import com.google.firebase.firestore.CollectionReference;

public class HomeFragment extends Fragment implements FirestoreUpdateListener {
    private CollectionReference itemRef;
    private ListView itemListView;
    private PopupMenu filterView;
    private ArrayAdapter<Item> itemAdapter;

    public HomeFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), new ItemList(this, itemRef).getItems());
        itemListView.setAdapter(itemAdapter);

        View filterButton = rootView.findViewById(R.id.filter_dropdown_button);
        filterButton.setOnClickListener(v -> showFilterMenu(v));

        return rootView;

    }

    @Override
    public void notifyDataSetChanged() {
        itemAdapter.notifyDataSetChanged();
    }

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

}
