package com.example.root.dailywallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    ImageView mView;
    EditText mEditText;
    Button mButton;

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mView.setImageBitmap(result);
            try {
                WallpaperManager.getInstance(getApplicationContext()).setBitmap(result);
            } catch (IOException e) {
                // TODO Better Handle Errors
                e.printStackTrace();
            }
        }
    }

    //Will get the top 25 posts from a subreddit, creating an array of urls
    private void queryReddit(String subreddit){
        subreddit = subreddit.replaceAll("\\s","");
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
                            int num_kids = response.getJSONObject("data").getInt("dist");
                            if (num_kids > 0){
                                JSONArray kids = response.getJSONObject("data").getJSONArray("children");
                                String[] urls = new String[num_kids];
                                for(int i =0; i<num_kids; i++) {
                                    response = kids.getJSONObject(i).getJSONObject("data");
                                    if (response.getString("post_hint").equals("image")) {
                                        urls[i] = response.getString("url");
                                    }
                                }
                                mTextView.setText(urls[0]);
                                new DownloadImage().execute(urls);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.subreddit_error, Toast.LENGTH_SHORT).show();
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
        mView = (ImageView) findViewById(R.id.imageView1);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = findViewById(R.id.button);
        queryReddit("");

    }
        public void buttonClick(View v) {
            queryReddit(mEditText.getText().toString());
        }
}



