package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.deletePhotosFromCloud;
import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static java.util.Optional.ofNullable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househomey.form.EditItemFragment;

import com.example.househomey.form.ViewPhotoAdapter;
import com.example.househomey.tags.Tag;
import com.example.househomey.tags.ApplyTagFragment;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This fragment is for the "View Item Page" - which currently displays the details and comment linked
 * to the item
 * @author Matthew Neufeld
 */
public class ViewItemFragment extends Fragment {
    private Item item;
    protected ViewPhotoAdapter viewPhotoAdapter;
    private CollectionReference tagRef;
    private ChipGroup chipGroup;

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
     * @return view item fragment
     */
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_item, container, false);
        tagRef = ((MainActivity) requireActivity()).getTagRef();
        tagRef.addSnapshotListener(this::setupTagListener);
        // Initialize TextViews
        TextView title = rootView.findViewById(R.id.view_item_title);
        TextView make = rootView.findViewById(R.id.view_item_make);
        TextView model = rootView.findViewById(R.id.view_item_model);
        TextView serialNumber = rootView.findViewById(R.id.view_item_serial_number);
        TextView cost = rootView.findViewById(R.id.view_item_cost);
        TextView comment = rootView.findViewById(R.id.view_item_comment);
        TextView noPhotosView = rootView.findViewById(R.id.view_item_no_photos);
        ImageView mainPhoto = rootView.findViewById(R.id.view_item_main_photo);
        chipGroup = rootView.findViewById(R.id.tag_chip_group_labels);

        // Set TextViews to Item details sent over from ItemAdapter
        ofNullable(getArguments())
                .map(args -> args.getSerializable("item", Item.class))
                .ifPresent(item -> {
                    this.item = item;
                    title.setText(item.getDescription());
                    make.setText(item.getMake());
                    model.setText(item.getModel());
                    serialNumber.setText(item.getSerialNumber());
                    cost.setText(item.getCost().toString());
                    addTagsToChipGroup();
                    comment.setText(item.getComment());
                });

        // On edit button click, pass item to EditItemFragment
        rootView.findViewById(R.id.edit_button).setOnClickListener(v ->
                navigateToFragmentPage(getContext(), new EditItemFragment(item))
        );

        rootView.findViewById(R.id.delete_button).setOnClickListener(v -> {
                    deletePhotosFromCloud(requireActivity(), item.getPhotoIds());
                    ((MainActivity) requireActivity()).getItemRef().document(item.getId()).delete();
                    navigateToFragmentPage(getContext(), new HomeFragment());
                }
        );
        rootView.findViewById(R.id.view_item_back_button).setOnClickListener(v -> navigateToFragmentPage(getContext(), new HomeFragment()));

        viewPhotoAdapter = new ViewPhotoAdapter(getContext(), item.getPhotoIds(), imagePath -> viewPhotoAdapter.loadIntoImageView(mainPhoto, imagePath));
        if (item.getPhotoIds().isEmpty()) {
            noPhotosView.setVisibility(View.VISIBLE);
            mainPhoto.setVisibility(View.GONE);
        } else {
            noPhotosView.setVisibility(View.GONE);
            mainPhoto.setVisibility(View.VISIBLE);
            viewPhotoAdapter.loadIntoImageView(mainPhoto, item.getPhotoIds().get(0));
        }
        ((RecyclerView) rootView.findViewById(R.id.view_photo_grid)).setAdapter(viewPhotoAdapter);

        final Button addTagsButton = rootView.findViewById(R.id.add_tags_button);
        addTagsButton.setOnClickListener(v -> {
            ApplyTagFragment applyTagFragment = new ApplyTagFragment();

            ArrayList<Item> selectedItem = new ArrayList<>();
            selectedItem.add(item);
            Bundle tagArgs = new Bundle();
            tagArgs.putParcelableArrayList("itemList", selectedItem);
            applyTagFragment.setArguments(tagArgs);
            applyTagFragment.show(requireActivity().getSupportFragmentManager(),"tagDialog");
        });

        return rootView;
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
            item.clearTags();
            chipGroup.removeAllViews();
            for (QueryDocumentSnapshot doc: querySnapshots) {
                Tag tag = new Tag(doc.getId(), doc.getData());
                for (String id : tag.getItemIds()) {
                    if (Objects.equals(item.getId(), id)) item.addTag(tag);
                }
            }
            addTagsToChipGroup();
        }
    }

    /**
     * Adds tags such that they can be viewed on the view item page. Also enables tags to be deleted when clicking the close icon.
     */
    private void addTagsToChipGroup() {
        CollectionReference tagRef = ((MainActivity) requireActivity()).getTagRef();
        for (Tag tag: item.getTags()) {
            final Chip chip = FragmentUtils.makeChip(tag.getTagLabel(), true, chipGroup, getContext(), R.color.creme, R.color.black, R.color.black);
            final Tag finalTag = tag;
            chip.setOnCloseIconClickListener(v -> {
                tagRef.document(finalTag.getTagLabel()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> itemIds = new ArrayList<>((List<String>) task.getResult().get("items"));
                        itemIds.removeIf(item -> item.equals(this.item.getId()));
//                        chipGroup.removeView(chip);
                        tagRef.document(finalTag.getTagLabel()).update("items", itemIds);
                    }
                });
            });
        }
    }
}
