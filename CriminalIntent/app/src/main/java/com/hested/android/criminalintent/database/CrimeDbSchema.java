package com.hested.android.criminalintent.database;

/**
 * Project:  CriminalIntent
 * Package:  com.hested.android.criminalintent.database
 * Date:     18-03-2017
 * Time:     04:15
 * Author:   Johnni Hested
 */

public class CrimeDbSchema {

    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
