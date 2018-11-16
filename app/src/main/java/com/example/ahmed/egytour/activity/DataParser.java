package com.example.ahmed.egytour.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ahmed.egytour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.ahmed.egytour.activity.MainActivity.logos;

public class DataParser extends AsyncTask<Void, Void, Boolean> {
    static String longitude;
    static String latitude;
    static JSONArray jsonArr;
    Context c;
    static String jsonData;
    ListView lv;

    ProgressDialog pd;

    static ArrayList<String> spacecrafts = new ArrayList<>();

    public DataParser(Context c, String jsonData, ListView lv) {
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Pasring..Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parsedata();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        pd.dismiss();
        if (result) {
            CustomAdapter customAdapter = new CustomAdapter(c, spacecrafts, logos);
            lv.setAdapter(customAdapter);


        }
    }

    private Boolean parsedata() {
        try {
            jsonArr = new JSONArray(jsonData);
            JSONObject jo;
            logos.clear();
            spacecrafts.clear();
            for (int i = 0; i < jsonArr.length(); i++) {

                jo = jsonArr.getJSONObject(i);

                String name = jo.getString("name") + "  ";
                name += jo.getString("Rating");
                spacecrafts.add(name);
                String logo = jo.getString("Imagepath");
                int resID = c.getResources().getIdentifier(logo, "drawable", c.getPackageName());
                logos.add(resID);

            }

            return true;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}