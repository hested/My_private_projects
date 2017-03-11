package com.hested.android.shoppinglist;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Project:  ShoppingList
 * Package:  com.hested.android.shoppinglist
 * Date:     03-03-2017
 * Time:     22:34
 * Author:   Johnni Hested
 */

public class ShoppingActivity extends AppCompatActivity {
    private static final String TAG = "ShoppingActivity";

    private TextView mItemName, mItemQuantity;
    private Button mAddItem;
    private ListView mShoppingListView;
    private ArrayAdapter<ShoppingItem> mAdapter;
    private List<ShoppingItem> mShoppingList = new ArrayList<>();
    private String mUrlString = "https://itu.dk/people/jacok/mmad/services/shoppinglist/";
    private ShoppingItem mShoppingItem;
    private boolean mPostStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        // Application version number
        TextView versionNumber = (TextView) findViewById(R.id.version_text_view);
        String version = "Not available";
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionNumber.append(version);

        // Text fields for describing a shopping item
        mItemName = (TextView) findViewById(R.id.name_text);
        mItemQuantity = (TextView) findViewById(R.id.quantity_text);
        mItemQuantity.setText("1");

        // Add item button
        mAddItem = (Button) findViewById(R.id.add_item_button);

        // Add a shopping item to the list
        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemName.getText().length() > 0 && mItemQuantity.getText().length() > 0 ) {
                    mShoppingItem = new ShoppingItem(mItemName.getText().toString(), Integer.parseInt(mItemQuantity.getText().toString()));

                    new JSONPost().execute(mUrlString);

                }
            }
        });

        // ListView for the shopping list
        mShoppingListView = (ListView) findViewById(R.id.shopping_list_view);

        // Instantiate adapter
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mShoppingList);

        // Loading list from the url
        new JSONGet().execute(mUrlString);
    }

    public class JSONGet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // Connecting to remote database
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer stringBuffer = new StringBuffer(); //TODO change to StringBuilder
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                // Making a JSON string from the buffered strings
                String finalJson = stringBuffer.toString();

                // Creating a JSON array with objects from the JSON string
                JSONArray jsonArray = new JSONArray(finalJson);

                // Adding each JSON object to the shopping list
                mShoppingList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int itemId = jsonObject.getInt("ID"); // Not used at the moment
                    String itemName = jsonObject.getString("item");
                    int itemAmount = jsonObject.getInt("amount");
                    mShoppingList.add(new ShoppingItem(itemName, itemAmount));
                }

                return null;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Assign adapter to ListView
            mShoppingListView.setAdapter(mAdapter);
        }
    }

    public class JSONPost extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            BufferedReader reader = null;

            try {
                //Create JSONObject here
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("item", mShoppingItem.getItemName());
                jsonObject.put("amount", mShoppingItem.getQuantity());

                // Connect and post to URL
                URL url = new URL(mUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                // Get the status response from the server
                inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // Checks for OK status
                if (reader.readLine().equals("{\"status\":\"OK\"}")) {
                    mPostStatus = true;
                }

                return reader.readLine();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // If post succeeded, Getting the new shopping list
            if (mPostStatus) {
                Toast.makeText(ShoppingActivity.this, "Added: " + mShoppingItem.toString(), Toast.LENGTH_LONG).show();
                mPostStatus = false;
                mAdapter.notifyDataSetChanged();
                mItemName.setText("");
                mItemQuantity.setText("1");
                new JSONGet().execute(mUrlString);
            } else {
                Toast.makeText(ShoppingActivity.this, "Could not add: " + mShoppingItem.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
