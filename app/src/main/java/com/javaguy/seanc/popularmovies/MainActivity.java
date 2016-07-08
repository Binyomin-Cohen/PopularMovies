package com.javaguy.seanc.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Picasso.with(getApplicationContext()).load("http://webmachers.tech/me.jpg").into(imageView);

        String url = "https://api.themoviedb.org/3/movie/550?api_key=INSERT_API_KEY_HERE";
        MovieGetterTask mgt = new MovieGetterTask();
        mgt.execute(url);
    }
    private class MovieGetterTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            URL movieUrl = null;
            HttpURLConnection movieConnection = null;
            try{
                movieUrl = new URL(url);
                movieConnection = (HttpURLConnection)movieUrl.openConnection();
                Log.d("MovieGetterTask", "got here");
                Scanner s = new Scanner(movieConnection.getInputStream()).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                return result;
            }
            catch (Exception e){
                Log.e("MovieGetterTask", e.toString());
            }
            finally {
                movieConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
