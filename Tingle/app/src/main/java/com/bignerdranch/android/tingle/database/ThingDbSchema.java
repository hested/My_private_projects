package com.bignerdranch.android.tingle.database;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle.database
 * Date:     23-03-2017
 * Time:     12:42
 * Author:   Johnni Hested
 */

public class ThingDbSchema {
    public static final class ThingTable {
        public static final String NAME = "things";

        public static final class Cols {
            public static final String UUID = "_uuid";
            public static final String WHAT = "_what";
            public static final String WHERE = "_where";
        }
    }
}
