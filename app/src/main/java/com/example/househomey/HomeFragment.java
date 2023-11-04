package com.example.househomey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        filterButton.setOnClickListener(v -> showPopupMenu(v));

        return rootView;
    }

    @Override
    public void notifyDataSetChanged() {
        itemAdapter.notifyDataSetChanged();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.filter, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        popupMenu.show();
    }

}
