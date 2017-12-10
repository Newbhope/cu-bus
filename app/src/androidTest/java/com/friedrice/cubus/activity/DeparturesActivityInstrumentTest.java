package com.friedrice.cubus.activity;

import android.os.Build;

import org.junit.Before;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by newho on 11/10/2016.
 */

public class DeparturesActivityInstrumentTest {
    @Before
    public void grantMapPermission() {
        // Make sure the permission is granted before running this test.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.Manifest.permission.ACCESS_FINE_LOCATION");
        }
    }

//    @Rule
//    public ActivityTestRule<DeparturesActivity> mActivityTestRule = new ActivityTestRule<>(DeparturesActivity.class);

//    @Test
//    public void test() {
//        ViewInteraction favoritesButton = onView(allOf(withId(R.id.fav))).perform(click());
//        favoritesButton.check(matches(isDisplayed()));
//    }
}
