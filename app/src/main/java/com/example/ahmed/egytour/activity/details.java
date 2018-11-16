package com.example.ahmed.egytour.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.egytour.R;

import java.util.Locale;

import static com.example.ahmed.egytour.activity.MainActivity.latitude;
import static com.example.ahmed.egytour.activity.MainActivity.longitude;

public class details extends Activity {
    static EditText reviewtext;
    static EditText ratingtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        reviewtext = (EditText) findViewById(R.id.reviewtext);
        ratingtext = (EditText) findViewById(R.id.ratingtext);
        Button post = (Button) findViewById(R.id.buttonpost);
        ImageView logo = (ImageView) findViewById(R.id.logo);
            textView1.setText("        " +MainActivity.destname);
        textView2.setText("Average Rating: "+MainActivity.Rating);
        textView3.setText("Location" + ":" + latitude + "," + longitude);
        textView4.setText("Phonenumber:"+MainActivity.Phonenum);
        if (MainActivity.reviews != null)
            textView5.setText("Reviews: "+MainActivity.reviews);
        logo.setImageResource(MainActivity.resID);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String method = "review";
                if (ratingtext.getText() == null)
                    Toast.makeText(details.this, "Please enter rating", Toast.LENGTH_LONG).show();
                else {
                    BackgroundTask backgroundTask = new BackgroundTask(details.this);
                    backgroundTask.execute(method, MainActivity.destname, reviewtext.getText().toString() + '\n',ratingtext.getText().toString() );
                }
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(latitude), Double.parseDouble(longitude));
                // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                //startActivity(intent);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.parseDouble(latitude) + "," + Double.parseDouble(longitude));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }


}
