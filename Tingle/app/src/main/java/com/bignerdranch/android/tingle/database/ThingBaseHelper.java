package com.bignerdranch.android.tingle.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle.database
 * Date:     23-03-2017
 * Time:     12:53
 * Author:   Johnni Hested
 */

public class ThingBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "thingBase.db";

    public ThingBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + ThingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ThingTable.Cols.UUID + ", " +
                ThingTable.Cols.WHAT + ", " +
                ThingTable.Cols.WHERE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
