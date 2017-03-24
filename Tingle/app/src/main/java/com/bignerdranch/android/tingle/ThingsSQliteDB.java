package com.bignerdranch.android.tingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.tingle.database.ThingBaseHelper;
import com.bignerdranch.android.tingle.database.ThingCursorWrapper;
import com.bignerdranch.android.tingle.database.ThingDbSchema.ThingTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     23-03-2017
 * Time:     13:43
 * Author:   Johnni Hested
 */

public class ThingsSQLiteDB {
    private static ThingsSQLiteDB sThingsSQLiteDB;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    static ThingsSQLiteDB get(Context context) {
        if (sThingsSQLiteDB == null) {
            sThingsSQLiteDB = new ThingsSQLiteDB(context);
        }

        return sThingsSQLiteDB;
    }

    private ThingsSQLiteDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ThingBaseHelper(mContext).getWritableDatabase();
    }

    void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);
        mDatabase.insert(ThingTable.NAME, null, values);
    }

    boolean removeThing(Thing thing) {
        return mDatabase.delete(ThingTable.NAME, ThingTable.Cols.UUID + " = '" + thing.getId().toString() + "'", null) > 0;
    }

    Thing getThing(UUID id) {
        ThingCursorWrapper cursor = queryThings(
                ThingTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getThing();
        } finally {
            cursor.close();
        }
    }

    List<Thing> getThings() {
        List<Thing> things = new ArrayList<>();
        ThingCursorWrapper cursor = queryThings(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                things.add(cursor.getThing());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return things;
    }

    void updateThing(Thing thing) {
        String uuidString = thing.getId().toString();
        ContentValues values = getContentValues(thing);
        mDatabase.update(ThingTable.NAME, values,
                ThingTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Thing thing) {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());
        return values;
    }

    private ThingCursorWrapper queryThings(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ThingTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ThingCursorWrapper(cursor);
    }
}
