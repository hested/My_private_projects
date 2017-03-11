package com.hested.android.shoppinglist;

/**
 * Project:  ShoppingList
 * Package:  com.hested.android.shoppinglist
 * Date:     04-03-2017
 * Time:     00:11
 * Author:   Johnni Hested
 */

public class ShoppingItem {
    private String mItemName;
    private int mQuantity;

    public ShoppingItem(String itemName, int quantity) {
        mItemName = itemName;
        mQuantity = quantity;
    }

    @Override
    public String toString() {
        return mItemName + " (" + mQuantity + ")";
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }
}
