package com.bignerdranch.android.tingle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import com.bignerdranch.android.tingle.Thing;
import com.bignerdranch.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle.database
 * Date:     23-03-2017
 * Time:     13:03
 * Author:   Johnni Hested
 */

public class ThingCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ThingCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Thing getThing() {
        String uuidString = getString(getColumnIndex(ThingTable.Cols.UUID));
        String what = getString(getColumnIndex(ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(ThingTable.Cols.WHERE));

        Thing thing = new Thing(UUID.fromString(uuidString));
        thing.setWhat(what);
        thing.setWhere(where);

        return thing;
    }
}
