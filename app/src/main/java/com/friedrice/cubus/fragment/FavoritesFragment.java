package com.friedrice.cubus.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.friedrice.cubus.R;
import com.friedrice.cubus.activity.DeparturesActivity;
import com.friedrice.cubus.adapter.ItemAdapter;
import com.friedrice.cubus.util.GsonFileUtils;
import com.google.gson.Gson;
import com.woxthebox.draglistview.DragListView;

import java.io.IOException;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    RelativeLayout instructions;

    private ArrayList<Pair<Long, String>> mItemArray;
    private DragListView listView;
    private GsonFileUtils gsonFileUtils = new GsonFileUtils();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        instructions = rootView.findViewById(R.id.instructions);
        listView = rootView.findViewById(R.id.favorites_list);

        return rootView;
    }

    @Override
    public void onResume() {
        populateFavorites();
        setUpListView();
        super.onResume();
    }

    private void populateFavorites() {

        String filename = "favorites.json";
        String json = null;
        try {
            json = gsonFileUtils.getJsonStringFromFile(filename, getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        ArrayList favoritesList = gson.fromJson(json, ArrayList.class);
        mItemArray = new ArrayList<>();
        for(int i = 0; i < favoritesList.size(); i++) {
            mItemArray.add(new Pair<>((long) i, (String) favoritesList.get(i)));
        }

        if (favoritesList.size() == 0) {
            listView.setVisibility(View.GONE);
            instructions.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            instructions.setVisibility(View.GONE);
        }

    }
    private void setUpListView() {
        //DragListView option setting
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setCanDragHorizontally(false);
        final ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_favorite_item,
                R.id.item_layout, true, new ItemAdapter.ItemInterface() {
            @Override
            public void itemClicked(String stopName) {
                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                Intent intent = new Intent(getActivity(), DeparturesActivity.class);
                intent.putExtra("title", stopName);
                String stopId = sharedPreferences.getString(stopName, "error");
                intent.putExtra("id", stopId);
                startActivity(intent);
            }
        });
        listView.setAdapter(listAdapter, true);

        listView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                ArrayList<String> changedList = new ArrayList<String>();
                ArrayList<Pair<Long, String>> list = (ArrayList<Pair<Long, String>>) listAdapter.getItemList();
                for(Pair<Long, String> pair : list) {
                    changedList.add(pair.second);
                }

                gsonFileUtils.writeListToFile(changedList, getActivity());
            }
        });
    }


}
