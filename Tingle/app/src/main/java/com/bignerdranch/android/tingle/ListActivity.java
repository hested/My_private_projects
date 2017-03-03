package com.bignerdranch.android.tingle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     17-02-2017
 * Time:     08:34
 * Author:   Johnni Hested
 */

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ThingsDB thingsDB = ThingsDB.get(this);
        Log.d(TAG, "Database ID: " + System.identityHashCode(thingsDB));
        mListView = (ListView) findViewById(R.id.list_things_view);

        // Instantiate custom adapter
        CustomAdapter adapter = new CustomAdapter(this);

        // Assign adapter to ListView
        mListView.setAdapter(adapter);
    }
}
