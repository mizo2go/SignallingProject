package com.example.ahmed.egytour.activity;
import com.example.ahmed.egytour.R;

import com.example.ahmed.egytour.helper.SQLiteHandler;
import com.example.ahmed.egytour.helper.SessionManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.ahmed.egytour.activity.DataParser.jsonData;
import static com.example.ahmed.egytour.activity.DataParser.spacecrafts;
import static java.util.Collections.sort;

public class MainActivity extends Activity {
    final static String urlAddress="http://192.168.1.2/android_login_api/getplaces.php";

    private Switch sortswitch;
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnmap;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView lv= (ListView) findViewById(R.id.lv);

        sortswitch=(Switch) findViewById(R.id.switch1);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnmap = (Button) findViewById(R.id.btnmap);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        btnmap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        sortswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JSONArray jsonArr = null;
                try {
                    jsonArr = new JSONArray(jsonData);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                JSONObject jo;
                JSONArray sortedJsonArray = new JSONArray();

                List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    try {
                        jsonValues.add(jsonArr.getJSONObject(i));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID
                    private static final String KEY_NAME = "Rating";

                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        String valA = new String();
                        String valB = new String();

                        try {
                            valA = (String) a.get(KEY_NAME);
                            valB = (String) b.get(KEY_NAME);
                        } catch (JSONException e) {
                            //do something
                        }

                        return valB.compareTo(valA);
                        //if you want to change the sort order, simply use the following:
                        //return -valA.compareTo(valB);
                    }
                });

                for (int i = 0; i < jsonArr.length(); i++) {
                    sortedJsonArray.put(jsonValues.get(i));
                }
                spacecrafts.clear();
                for (int i = 0; i < sortedJsonArray.length(); i++) {

                    try {
                        jo = sortedJsonArray.getJSONObject(i);


                        String name = jo.getString("name") + " ";
                        name += jo.getString("Rating");
                        spacecrafts.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



                ArrayAdapter adapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,spacecrafts);
                lv.setAdapter(adapter);

            }
        });
        new Downloader(MainActivity.this,urlAddress,lv).execute();
    }
    public static Object connect(String urlAddress)
    {
        try
        {
            URL url=new URL(urlAddress);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();

            //SET CON PROPERTIES
            con.setRequestMethod("GET");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setDoInput(true);

            return con;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error "+e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error "+e.getMessage();

        }
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}