package com.bignerdranch.android.tingle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     23-02-2017
 * Time:     12:15
 * Author:   Johnni Hested
 */

public class TingleActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentTingle = fm.findFragmentById(R.id.fragment_tingle_container);
        Fragment fragmentList = fm.findFragmentById(R.id.fragment_list_container);

        if (fragmentTingle == null && fragmentList == null) {

            fragmentTingle = new TingleFragment();
            fragmentList = new ListFragment();

            fm.beginTransaction().add(R.id.fragment_tingle_container, fragmentTingle).commit();
            fm.beginTransaction().add(R.id.fragment_list_container, fragmentList).commit();
        }
    }
}
