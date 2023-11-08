package com.example.househomey.testUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

import org.hamcrest.Matcher;

public class EspressoWait {
    private static final long TIMEOUT = 5000; // Timeout in milliseconds
    private static final long POLLING_INTERVAL = 500; // Polling interval in milliseconds

    public static void waitForView(Matcher<View> viewMatcher) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        // Try to match multiple times during timeout
        while (elapsedTime < TIMEOUT) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                return; // View is displayed, exit
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException ignore) {
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Try one last time, otherwise error out with added description
        try {
            onView(viewMatcher).check(matches(isDisplayed()));
        } catch (Exception e) {
            throw new AssertionError("View matching the given Matcher was not displayed within the timeout. " + e);
        }
    }
}

