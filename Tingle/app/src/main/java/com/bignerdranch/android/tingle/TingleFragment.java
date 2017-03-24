package com.bignerdranch.android.tingle;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     21-02-2017
 * Time:     17:59
 * Author:   Johnni Hested
 */

public class TingleFragment extends Fragment {

    // GUI variables
    private Button mAddNewThing, mFindThing, mListAllThings;
    private TextView mLastAdded, mNewWhat, mNewWhere;

    // Using SQLite database
    private static ThingsSQLiteDB sThingsSQLiteDB; //TODO Should this be private static?

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tingle, container, false);

        sThingsSQLiteDB = ThingsSQLiteDB.get(getActivity());

        // Accessing GUI element
        mLastAdded = (TextView) view.findViewById(R.id.last_text);
        updateUI();

        // Button
        mAddNewThing = (Button) view.findViewById(R.id.add_button);
        mFindThing = (Button) view.findViewById(R.id.find_button);
        mListAllThings = (Button) view.findViewById(R.id.list_all_things_button);

        // Text fields for describing a thing
        mNewWhat = (TextView) view.findViewById(R.id.what_text);
        mNewWhere = (TextView) view.findViewById(R.id.where_text);

        // Application version number
        TextView versionNumber = (TextView) view.findViewById(R.id.version_text_view);
        String version = "Not available";
        PackageInfo pInfo;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionNumber.append(version);

        // View an added Thing on click event
        mAddNewThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mNewWhat.getText().length() > 0 && mNewWhere.getText().length() > 0 ) {

                    sThingsSQLiteDB.addThing(
                            new Thing(
                            mNewWhat.getText().toString(),
                            mNewWhere.getText().toString()
                    ));

                    // Updating the ListFragment view
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.fragment_list_container, new ListFragment()).commit();

                    // Resetting the text fields
                    mNewWhat.setText("");
                    mNewWhere.setText("");

                    updateUI();
                }
            }
        });

        // Find an added Thing on click event
        mFindThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mNewWhat.getText().length() > 0) {
                    String searchWord = mNewWhat.getText().toString();
                    String showWhere = getString(R.string.cant_find_the_thing);

                    // Loops through the things in the database
                    for (Thing thingInDatabase : sThingsSQLiteDB.getThings()) {
                        if (searchWord.equals(thingInDatabase.getWhat())) {
                            showWhere = thingInDatabase.toString();
                        }
                    }

                    Toast.makeText(getActivity(), showWhere, Toast.LENGTH_LONG).show();

                    // Resetting the text fields
                    mNewWhat.setText("");
                    mNewWhere.setText("");

                    updateUI();
                }
            }
        });

        // Show a list of things that have been added
        mListAllThings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ListActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    // Method to update the UI view
    private void updateUI(){
        int databaseSize = sThingsSQLiteDB.getThings().size();
        if (databaseSize > 0) {
            mLastAdded.setText(sThingsSQLiteDB.getThings().get(databaseSize - 1).toString());
        }
    }
}
