package com.example.represent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class congress extends AppCompatActivity {

    public static final String PERSON = "com.example.represent.PERSON";

    StrictMode.ThreadPolicy policy;




    public RequestQueue queue;
    public JSONObject Sen1, Sen2, Rep;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congress);
        queue = Volley.newRequestQueue(this);
        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        try {
            Sen1 = new JSONObject(Objects.requireNonNull(intent.getStringExtra(MainActivity.SEN1)));
            Sen2 = new JSONObject(Objects.requireNonNull(intent.getStringExtra(MainActivity.SEN2)));
            Rep = new JSONObject(Objects.requireNonNull(intent.getStringExtra(MainActivity.REP)));
            populateInfo(Sen1, (ImageView) findViewById(R.id.roundedimage1), (TextView) findViewById(R.id.candidate1), (TextView) findViewById(R.id.party1));
            populateInfo(Sen2, (ImageView) findViewById(R.id.roundedimage2), (TextView) findViewById(R.id.candidate2), (TextView) findViewById(R.id.party2));
            populateInfo(Rep, (ImageView) findViewById(R.id.roundedimage3), (TextView) findViewById(R.id.candidate3), (TextView) findViewById(R.id.party3));

        } catch (JSONException e) {

        }

    }

    public void populateInfo(JSONObject congressman, ImageView image, TextView title, TextView party) throws JSONException {
        try {
            String url = (String) congressman.get("photoUrl");
            getPicture(url, image);
        } catch (Exception e) {
            image.setImageResource(R.drawable.picture_error);
        }

        title.setText(congressman.get("name") + ", ");
        char partyChar = ((String)congressman.get("party")).charAt(0);
        party.setText("" + partyChar);
        switch (partyChar) {
            case 'D': party.setTextColor(Color.BLUE);
                break;
            case 'R': party.setTextColor(Color.RED);
                break;
            default: party.setTextColor(Color.GREEN);
                break;
        }
    }

    public void getPicture(String url, ImageView image) {
        Drawable urlImage = LoadImageFromWebOperations(url);
        if (urlImage != null) {
            image.setImageDrawable(urlImage);
        }
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void onTap1(View view) {
        goToDetails(Sen1);
    }

    public void onTap2(View view) {
        goToDetails(Sen2);
    }

    public void onTap3(View view) {
        goToDetails(Rep);
    }

    private void goToDetails(JSONObject person) {
        Intent intent = new Intent(this, Detail.class);
        intent.putExtra(PERSON, person.toString());
        startActivity(intent);
    }

}