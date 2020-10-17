package com.example.represent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyCogCky5dQdw0z4HzGFDC-tg1RD6sRdu84";
    public static final String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    public static final String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public static final String REP = "com.example.represent.REPRESENTATIVE";
    public static final String SEN1 = "com.example.represent.SENATOR1";
    public static final String SEN2 = "com.example.represent.SENATOR2";

    private FusedLocationProviderClient fusedLocationClient;


    public RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        queue = Volley.newRequestQueue(this);
/*
        ActivityResultContract contract;
        ActivityResultCallback callback;
        ActivityResultLauncher requestPermissionLauncher = registerForActivityResult()
*/
    }

    public void lookupZIP(View view) {
        EditText editZipCode = findViewById(R.id.editZipCode);
        String zip = editZipCode.getText().toString();
        findCongress(zip);
    }

    public void randomZIP(View view) {
        String zip = Integer.toString((int) (Math.random() * (100000 - 1 + 1) + 100000));
        findCongress(zip);

    }

    public void findMyZIP(View view) {
/*        if (ContextCompat.checkSelfPermission(
                Context.LOCATION_SERVICE, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            continue;
        }  else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.REQUESTED_PERMISSION);
        }


        #enforcePermission();*/
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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            String url = GEO_URL + "?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&key=" + API_KEY;
                            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject json = new JSONObject(response);
                                                JSONArray results = (JSONArray) json.get("results");
                                                JSONObject components = (JSONObject) results.get(0);
                                                String address = (String) components.get("formatted_address");
                                                findCongress(address);
                                            } catch (JSONException e) {
                                                return;
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }
                });

    }

    private void findCongress(String address) {
        String url = CIVIC_URL + "?address=" + address + "&key=" + API_KEY;
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {
                        JSONObject[] Senators = {new JSONObject(), new JSONObject()};
                        JSONObject Representative = new JSONObject();

                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray offices = (JSONArray) json.get("offices");
                            JSONArray officials = (JSONArray) json.get("officials");
                            for (int i = 0; i < offices.length(); i++) {
                                JSONObject office = (JSONObject) offices.get(i);
                                if (office.get("name").equals("U.S. Senator")) {
                                    JSONArray indices =  (JSONArray) office.get("officialIndices");
                                    Senators[0] = (JSONObject) officials.get((int) indices.get(0));
                                    Senators[1] = (JSONObject) officials.get((int) indices.get(1));
                                } else if (office.get("name").equals("U.S. Representative")) {
                                    JSONArray indices =  (JSONArray) office.get("officialIndices");
                                    Representative = (JSONObject) officials.get((int) indices.get(0));
                                }
                            }
                            goToCongress(Senators, Representative);
                        } catch (JSONException e){
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }


    public void goToCongress(JSONObject[] Senators, JSONObject Representative){
        Intent intent = new Intent(this, congress.class);
        intent.putExtra(SEN1, Senators[0].toString());
        intent.putExtra(SEN2, Senators[1].toString());
        intent.putExtra(REP, Representative.toString());
        startActivity(intent);
    }

// Add the request to the RequestQueue.

}

