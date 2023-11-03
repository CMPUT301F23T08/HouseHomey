package com.example.househomey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements FirestoreUpdateListener {
    private FirebaseFirestore db;
    private ListView itemListView;
    private ArrayAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        // create or login a user, for now just assume...
        User user = new User(this,"john_doe");

        itemListView = findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(this, user.getItemList().getItems());
        itemListView.setAdapter(itemAdapter);


    }

    @Override
    public void notifyDataSetChanged() {
        itemAdapter.notifyDataSetChanged();
    }
}