package com.example.root.dailywallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    EditText mEditText;
    Button mButton;
    GridView simpleGrid;
    String[] urls;
    int num_kids;
    ImageAdapter imageAdapter;
    Bitmap[] bitmap;
    int objects = 0;

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... URL) {
            bitmap = new Bitmap[objects];
            for (int i = 0; i < objects; i++) {
                try {
                    InputStream input = new java.net.URL(URL[i]).openStream();
                    bitmap[i] = BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                WallpaperManager.getInstance(getApplicationContext()).setBitmap(bitmap[0]);
                imageAdapter = new ImageAdapter(getApplicationContext(), bitmap);
                simpleGrid.setAdapter(imageAdapter);
            } catch (IOException e) {
                // TODO Better Handle Errors
                e.printStackTrace();
            }
        }
    }

    //Will get the top 25 posts from a subreddit, creating an array of urls
    private void queryReddit(String subreddit){
        if(subreddit.equalsIgnoreCase("")){
            subreddit = "iphonewallpapers";
        }
        String url = "https://www.reddit.com/r/" + subreddit + "/top/.json";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Uses the response and checks if data is returned
                            num_kids = response.getJSONObject("data").getInt("dist");
                            if (num_kids > 0){
                                objects = 0;
                                JSONArray kids = response.getJSONObject("data").getJSONArray("children");
                                urls = new String[num_kids];
                                for(int i =0; i<num_kids; i++) {
                                    response = kids.getJSONObject(i).getJSONObject("data");
                                    if (response.getString("post_hint").equals("image")) {
                                        urls[i] = response.getString("url");
                                        objects++;
                                    }
                                }
                                mTextView.setText(urls[0]);
                                new DownloadImage().execute(urls);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.subreddit_error, Toast.LENGTH_SHORT).show();
                                //TODO account for 404 errors
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //TODO Better handle errors
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text1);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = findViewById(R.id.button);
        simpleGrid = (GridView) findViewById(R.id.gridView); // init GridView

        queryReddit("");

    }
        public void buttonClick(View v) {
            queryReddit(mEditText.getText().toString().replaceAll("\\s",""));
        }
}



