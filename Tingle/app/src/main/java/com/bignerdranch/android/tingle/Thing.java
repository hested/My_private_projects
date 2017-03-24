package com.bignerdranch.android.tingle;

import java.util.UUID;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     17-02-2017
 * Time:     10:14
 * Author:   Johnni Hested
 */

public class Thing {
    private final UUID mId;
    private String mWhat = null;
    private String mWhere = null;

    /**
     * Constructor for a Thing. It takes a string that
     * describes what the thing is and another string
     * that describes where the thing is located.
     *
     * @param what a string
     * @param where a string
     */
    public Thing(String what, String where) {
        mWhat = what;
        mWhere = where;
        mId = UUID.randomUUID();
    }

    public Thing(UUID id) {
        mId = id;
    }

    /**
     * A method that tells where a specific thing is located.
     *
     * @return a string
     */
    @Override
    public String toString() {
        return oneLine(" is here: ");
    }

    /**
     * Private method used to concatenate the What string
     * and the Where string.
     *
     * @param post a string
     * @return a string
     */
    private String oneLine(String post) {
        return mWhat + post + mWhere;
    }

    public String getWhat() {
        return mWhat;
    }

    public void setWhat(String what) {
        mWhat = what;
    }

    public String getWhere() {
        return mWhere;
    }

    public void setWhere(String where) {
        mWhere = where;
    }

    public UUID getId() {
        return mId;
    }
}
