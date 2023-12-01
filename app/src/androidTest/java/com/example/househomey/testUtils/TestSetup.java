package com.example.househomey.testUtils;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.househomey.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

/*
 * An abstract class for setting up Espresso test functionality required for both
 * Firebase and our Github Actions CI pipeline. Simply extend your test class with this class.
 * For methods that require a unique Firebase user, ensure the method name includes "WithNewUser"
 *
 * @author Owen Cooke
 */
public abstract class TestSetup {
    public MainActivity mainActivity;

    @Rule
    public DatabaseSetupRule<MainActivity> database = new DatabaseSetupRule<>(MainActivity.class);

    @Before
    public void dismissANRSystemDialog() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If the device is running in English Locale
        UiObject waitButton = device.findObject(new UiSelector().textContains("wait"));
        if (waitButton.exists()) {
            waitButton.click();
        }
    }

    @Before
    public void dismissAppCrashSystemDialogIfShown() {
        try {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                    .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS");
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }

    @Before
    public void setup() {
        mainActivity = database.setupActivity();
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}
