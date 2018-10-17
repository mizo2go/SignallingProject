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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.ahmed.egytour.activity.DataParser.jsonArr;
import static com.example.ahmed.egytour.activity.DataParser.jsonData;
import static com.example.ahmed.egytour.activity.DataParser.longitude;
import static com.example.ahmed.egytour.activity.DataParser.spacecrafts;
import static java.util.Collections.sort;

public class MainActivity extends Activity implements LocationListener {
    final static String urlAddress = "http://192.168.1.2/android_login_api/getplaces.php";
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Switch sortswitch;
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnmap;
    private Button distancebutton;


    private SQLiteHandler db;
    private SessionManager session;
    static ListView lv;
    static ArrayList<Float> distances = new ArrayList<>();
    static int z;
    static double latitude;
    static double longitude;
    static String destname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lv = (ListView) findViewById(R.id.lv);

        sortswitch = (Switch) findViewById(R.id.switch1);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnmap = (Button) findViewById(R.id.btnmap);
        distancebutton = (Button) findViewById(R.id.distancebutton);
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

        distancebutton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //  MapsActivity map=new MapsActivity();

                try {


                    JSONObject jo;
                    JSONArray jsonArray = new JSONArray(jsonData);

                    spacecrafts.clear();
                    float[] result = new float[1];
                    for (int i = 0; i < jsonArray.length(); i++) {


                        jo = jsonArray.getJSONObject(i);


                        String longi = jo.getString("Longitude");

                        String lat = jo.getString("Latitude");
                        Location.distanceBetween(latitude, longitude, Double.parseDouble(lat), Double.parseDouble(longi), result);
                        distances.add(result[0]);

                        // spacecrafts.add(name);

                    }
                    int k = 0;
                    while (jsonArray.length() != 0) {
                        float least = distances.get(0);
                        z = 0;
                        for (int j = 0; j < distances.size(); j++) {

                            if (distances.get(j) < least) {
                                least = distances.get(j);
                                z = j;

                            }

                        }

                        jo = jsonArray.getJSONObject(z);
                        String name = jo.getString("name") + " ";
                        name += jo.getString("Rating");
                        spacecrafts.add(name);
                        distances.remove(z);
                        jsonArray.remove(z);
                        k++;

                    }
                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, spacecrafts);
                    lv.setAdapter(adapter);
                } catch (JSONException e) {

                    e.printStackTrace();

                }
            }
        });
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
                DataParser.longitude = "";
                DataParser.latitude = "";
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jo;
                try {
                    for (int i = 0; i < jsonArr.length(); i++) {


                        jo = jsonArr.getJSONObject(i);
                        if (jo.getString("name").equals(spacecrafts.get(position).substring(0, spacecrafts.get(position).length() - 4))) {
                            DataParser.longitude = jo.getString("Longitude");
                            DataParser.latitude = jo.getString("Latitude");
                            destname=jo.getString("name");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(c, spacecrafts.get(position)+ " "+longitude+" "+latitude, Toast.LENGTH_SHORT).show();
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


                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, spacecrafts);
                lv.setAdapter(adapter);

            }
        });

        new Downloader(MainActivity.this, urlAddress, lv).execute();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    public static Object connect(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //SET CON PROPERTIES
            con.setRequestMethod("GET");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setDoInput(true);

            return con;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();

        }
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "disable");
    }
}