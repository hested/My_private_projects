package com.bignerdranch.android.tingle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Project:  Tingle
 * Package:  com.bignerdranch.android.tingle
 * Date:     27-02-2017
 * Time:     00:34
 * Author:   Johnni Hested
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private ThingsDB sThingsDatabase;
    private Context mContext;

    public CustomAdapter(Context context) {
        this.mContext = context;
        this.sThingsDatabase = ThingsDB.get(context);
    }

    @Override
    public int getCount() {
        return sThingsDatabase.size();
    }

    @Override
    public Object getItem(int pos) {
        return sThingsDatabase.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        //Handle TextView and display string from mList
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(sThingsDatabase.get(position).toString());
        listItemText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // ListView clicked item value
                Thing clickedThing = sThingsDatabase.get(position);

                // Shows the item's position number
                Toast.makeText(mContext,"Position " + position + ". Item : " + clickedThing.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sThingsDatabase.removeThing(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
