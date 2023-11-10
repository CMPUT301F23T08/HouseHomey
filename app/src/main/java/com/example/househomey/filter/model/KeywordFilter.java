package com.example.househomey.filter.model;

import com.example.househomey.Item;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A filter class that filters a list of items based on specific keywords.
 * @author Antonio Lech Martin-Ozimek
 */
public class KeywordFilter extends Filter{
    private ArrayList<String> keyWords;
    private ArrayList<String> ogKeyWords;

    /**
     * Constructs a new KeywordFilter with the specified keywords.
     *
     * @param keyWords The keywords to filter by.
     */
    public KeywordFilter(ArrayList<String> keyWords) {
        this.ogKeyWords = keyWords;
        this.keyWords = new ArrayList<>();
        for (String keyword : keyWords) {
            if (!Objects.equals(keyword, "")){this.keyWords.add(keyword.toLowerCase(Locale.ENGLISH));}
        }
    }

    public ArrayList<String> getOgKeyWords() {
        return ogKeyWords;
    }

    /**
     * Filters the passed in list of items, retaining only those items that match the
     * make value specified in this filter.
     *
     * @param itemList The original list of items to be filtered.
     * @return A filtered list of items that have at least one of the specified keywords.
     */
    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        return itemList.stream()
                .filter(item -> Arrays.stream(item.getDescription().toLowerCase(Locale.ENGLISH)
                        .split(" ")).anyMatch(keyWords::contains))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
