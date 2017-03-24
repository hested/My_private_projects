package com.hested.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.hested.android.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import static com.hested.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Project:  CriminalIntent
 * Package:  com.hested.android.criminalintent.database
 * Date:     18-03-2017
 * Time:     13:27
 * Author:   Johnni Hested
 */

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
