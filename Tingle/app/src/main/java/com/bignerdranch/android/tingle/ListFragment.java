package com.bignerdranch.android.tingle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     26-02-2017
 * Time:     20:19
 * Author:   Johnni Hested
 */

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_list_view);

        // Instantiate custom adapter
        CustomAdapter adapter = new CustomAdapter(getContext());

        // Assign adapter to ListView
        mListView.setAdapter(adapter);

        return view;
    }
}
