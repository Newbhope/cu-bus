package com.friedrice.cubus.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.friedrice.cubus.R;
import com.friedrice.cubus.fragment.FavoritesFragment;
import com.friedrice.cubus.fragment.MapFragment;
import com.friedrice.cubus.util.GsonFileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    Toolbar toolbar;

    Boolean fromDepartures = false;
    double stopLat = 0;
    double stopLon = 0;

    private GsonFileUtils gsonFileUtils = new GsonFileUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favorites");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        if (tabLayout != null) {
            setupTabLayout(tabLayout);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.getBoolean("map")) {
                tabLayout.getTabAt(1).select();
                fromDepartures = true;
                stopLat = extras.getDouble("busLat");
                stopLon = extras.getDouble("busLon");
            }
        }
        initializeFavoritesJson();
    }

    /**
     * initializes the "favorites.json" file if it doesn't exist
     */
    private void initializeFavoritesJson() {
        String filename = "favorites.json";
        FileInputStream inputStream;

        try {
            inputStream = openFileInput(filename);
            inputStream.close();
        } catch (FileNotFoundException e) { //this catch may not be what i'm thinking it is
            gsonFileUtils.writeListToFile(new ArrayList(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_favorite_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_map_white_24dp);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_directions_white_24dp);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int num = tab.getPosition();
                switch (num) {
                    case 0:
                        toolbar.setTitle(R.string.tab_title_favorites);
                        break;
                    case 1:
                        toolbar.setTitle(R.string.tab_title_map);
                        break;
                    case 2:
                        toolbar.setTitle(R.string.tab_title_routes);
                        break;
                }
                tab.getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN); //make favorites selected on creation
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorHeight(20);
    }

    @Override
    protected void onResume() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        colorOnlySelectedTab(tabLayout);
        super.onResume();
    }

    public void colorOnlySelectedTab(TabLayout tabLayout) {
        int selected = tabLayout.getSelectedTabPosition();
        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i != selected) {
                tabLayout.getTabAt(i).getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); //Puts search info into suggestions list
        searchView.setImeOptions(EditorInfo.IME_ACTION_NONE); //disables submit button

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FavoritesFragment();
                case 1:
                    if (fromDepartures) {
                        return MapFragment.newInstance(stopLat, stopLon, true);
                    } else {
                        return MapFragment.newInstance(stopLat, stopLon, false);
                    }
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show count total tabs.
            return 2;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for(Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(0, null, null);
            }
        }
    }

    @Override //android is really dumb
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for(Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


}
