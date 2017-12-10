package com.friedrice.cubus.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.friedrice.cubus.util.GsonFileUtils;

import java.util.ArrayList;

/**
 * Created by newho on 8/19/2016.
 */
public class ClearFavoritesDialogFragment extends DialogFragment {

    private GsonFileUtils gsonFileUtils = new GsonFileUtils();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("This will clear all of your favorite bus stops. Are you sure?")
                .setPositiveButton("Yes", new confirm())
                .setNegativeButton("No", null)
                .create();
    }
    public class confirm implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            gsonFileUtils.writeListToFile(new ArrayList(), getActivity());
        }
    }
}
