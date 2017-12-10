package com.friedrice.cubus.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.friedrice.cubus.R;

public class RoutesFragment extends Fragment {

    //TODO: implement week 2. Test driven development

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.routes_list);

        return rootView;
    }


}
