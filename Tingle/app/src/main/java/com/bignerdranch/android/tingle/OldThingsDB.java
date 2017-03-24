package com.bignerdranch.android.tingle;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     20-02-2017
 * Time:     22:56
 * Author:   Johnni Hested
 */

class OldThingsDB {
    private static OldThingsDB sOldThingsDB;


    // Fake database
    private List<Thing> mThingsDB;

    private OldThingsDB() {}

    static OldThingsDB get(Context context) {
        if (sOldThingsDB == null) {
            sOldThingsDB = new OldThingsDB(context);
        }
        return sOldThingsDB;
    }

    List<Thing> getThingsDB() {
        return mThingsDB;
    }

    void addThing(Thing thing) {
        mThingsDB.add(thing);
    }

    void removeThing(int index) {
        mThingsDB.remove(index);
    }

    int size() {
        return mThingsDB.size();
    }

    Thing get(int i) {
        return mThingsDB.get(i);
    }

    // Fill database for testing purposes
    private OldThingsDB(Context context) {
        mThingsDB = new ArrayList<>();
        mThingsDB.add(new Thing("Android Phone", "Desk"));
        mThingsDB.add(new Thing("Big Nerd book", "Desk"));
        mThingsDB.add(new Thing("Banana", "Refrigerator"));
        mThingsDB.add(new Thing("Milk", "Refrigerator"));
        mThingsDB.add(new Thing("Apples", "Refrigerator"));
        mThingsDB.add(new Thing("BMW", "Parking lot"));
        mThingsDB.add(new Thing("Television", "On the wall"));
        mThingsDB.add(new Thing("Car keys", "In your pocket"));
        mThingsDB.add(new Thing("Pencil", "Desk"));
        mThingsDB.add(new Thing("Painting", "On the wall"));
        mThingsDB.add(new Thing("Zoo", "In the city"));
        mThingsDB.add(new Thing("Bear", "In the Zoo"));
        mThingsDB.add(new Thing("My bottle", "In the house"));
        mThingsDB.add(new Thing("Big Book", "On the table"));
        mThingsDB.add(new Thing("Keyboard", "At the computer"));
        mThingsDB.add(new Thing("Chain", "In the toolbox"));
        mThingsDB.add(new Thing("Hammer", "In the toolbox"));
        mThingsDB.add(new Thing("Pants", "In the closet"));
        mThingsDB.add(new Thing("My dog", "Outside"));
    }
}
