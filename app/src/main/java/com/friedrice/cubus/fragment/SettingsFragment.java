package com.friedrice.cubus.fragment;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.friedrice.cubus.R;

/**
 * Created by newho on 11/14/2016.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        Preference clear = findPreference("clear");
        if(clear != null) {
            clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager fragmentManager = getFragmentManager();
                    ClearFavoritesDialogFragment dialog = new ClearFavoritesDialogFragment();
                    dialog.show(fragmentManager, "confirm");

                    return true;
                }
            });
        }
        Preference location = findPreference("location");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            preferenceScreen.removePreference(location);
        }
        if(location != null) {
            location.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(getActivity().getCurrentFocus(), "Location Enabled", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    return true;
                }
            });
        }
    }
}
