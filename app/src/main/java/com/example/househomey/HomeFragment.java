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

public class HomeFragment extends Fragment {
    private CollectionReference itemRef;
    private ListView itemListView;

    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayAdapter<Item> itemAdapter;

    public HomeFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

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
