package com.friedrice.cubus.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by newho on 11/2/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    MainActivity mainActivity;

    @Mock
    TabLayout tabLayout;

    @Before
    public void setUp() throws Exception {
        mainActivity = new MainActivity();
    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void onResume() throws Exception {
    }

    @Test
    public void onCreateOptionsMenu() throws Exception {

    }

    @Test
    public void onOptionsItemSelected() throws Exception {

    }

    @Test
    public void onResumeFragments() throws Exception {

    }

    @Test
    public void onRequestPermissionsResult() throws Exception {

    }

    @Test
    public void colorOnlySelectedTab() throws Exception {
        mainActivity.colorOnlySelectedTab(tabLayout);
        System.out.println(tabLayout.getTabCount());
    }

    @Mock
    FragmentManager fragmentManager;

    @Test
    public void testSectionsPagerAdapter() throws Exception {
        MainActivity.SectionsPagerAdapter adapter =
                mainActivity.new SectionsPagerAdapter(fragmentManager);
        assertEquals(2, adapter.getCount());
    }
}