package com.friedrice.cubus.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.friedrice.cubus.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setupActionBar();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            /*
              https://stackoverflow.com/questions/10320179/android-actionbar-up-button-versus-system-back-button
              Allows user to go back to whatever tab they were looking at instead of always being the favorites tab
              (Do I really want an action bar back button with the system back button? :shrug:
             */
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
