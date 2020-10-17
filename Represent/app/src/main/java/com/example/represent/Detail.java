package com.example.represent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class Detail extends AppCompatActivity {
    public RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        try {
            JSONObject person = new JSONObject(Objects.requireNonNull(intent.getStringExtra(congress.PERSON)));
            populateDetails(person);
        } catch (JSONException e) {
            return;
        }
    }

    public void populateDetails(JSONObject person) throws JSONException{
        ImageView image = findViewById(R.id.roundedimage);
        TextView name = findViewById(R.id.name);
        TextView party = findViewById(R.id.party);
        Button website_button = findViewById(R.id.website_button);
        Button phone_button = findViewById(R.id.phone_button);
        Button twitter_button = findViewById(R.id.twitter_button);
        Button youtube_button = findViewById(R.id.youtube_button);

        try {
            String url = (String) person.get("photoUrl");
            getPicture(url, image);
        } catch (Exception e) {
            image.setImageResource(R.drawable.picture_error);
        }
        name.setText((String) person.get("name"));
        party.setText((String) person.get("party"));
        char partyChar = ((String)person.get("party")).charAt(0);
        switch (partyChar) {
            case 'D': party.setTextColor(Color.BLUE);
                break;
            case 'R': party.setTextColor(Color.RED);
                break;
            default: party.setTextColor(Color.GREEN);
                break;
        }
        JSONArray urls = (JSONArray) person.get("urls");
        if (urls.length() > 0) {
            setButtonLink(website_button, (String) urls.get(0), false);
        }
        JSONArray phones = (JSONArray) person.get("phones");
        if (urls.length() > 0) {
            setButtonLink(phone_button, (String) phones.get(0), true);
        }
        JSONArray channels = (JSONArray) person.get("channels");
        for (int i = 0; i < channels.length(); i++) {
            JSONObject channel = (JSONObject) channels.get(i);
            switch ((String) channel.get("type")) {
                case "Twitter": setButtonLink(twitter_button, "https://twitter.com/" + channel.get("id"), false);
                    break;
                case "Youtube": setButtonLink(youtube_button, "https://www.youtube.com/" + channel.get("id"), false);
                    break;
            }
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


    public void setButtonLink(Button button, String string, boolean isPhone ) {
        button.setVisibility(View.VISIBLE);
        final String value = string;
        if (isPhone) {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+value));
                    startActivity(callIntent);
                }
            });
        }
        else {
            button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(value));
                        startActivity(browserIntent);
                    }
                });
        }
    }
}