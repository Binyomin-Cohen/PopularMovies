package com.javaguy.seanc.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> movies;  // this is the toString of the JSONObject of each movie
    JSONArray movieResponse;
    GridView gridView;
    CustArrAdapter arrAdapter;
    Context context = this;
    Fragment frag = null;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPrefereneceChangeListener;
    private SharedPreferences prefs = null;
    boolean byPopular = false;
    String urlPopular = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=APIKEY";
    String urlTopRated = "https://api.themoviedb.org/3/discover/movie?sort_by=top_rated.desc&api_key=APIKEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movies = null;
        movieResponse = null;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        gridView = (GridView)findViewById(R.id.gridView);


        MovieGetterTask mgt = new MovieGetterTask();
        mgt.execute(urlPopular);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        onSharedPrefereneceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                byPopular = "pop".equals(prefs.getString("sortBy", "pop"));

                MovieGetterTask mgt = new MovieGetterTask();
                if(byPopular ){
                    mgt.execute(urlPopular);
                }
                else{
                    mgt.execute(urlTopRated);
                }
            }

        };

        prefs.registerOnSharedPreferenceChangeListener(onSharedPrefereneceChangeListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .addToBackStack("settings")
                .commit();
        return true;
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


            try {

                JSONObject jsonObject = new JSONObject(result);
                movieResponse = jsonObject.getJSONArray("results");
                movies = new ArrayList<String>();

                for(int i = 0; i < movieResponse.length(); ++i){
                    movies.add(movieResponse.getJSONObject(i).toString());
                }
                arrAdapter = new CustArrAdapter(context, R.layout.movie_home_page, movies.toArray());
                gridView.setAdapter(arrAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String movieDetails = parent.getAdapter().getItem(position).toString();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra("MovieDetails", movieDetails);
                        startActivity(intent);
                    }
                });
            }
            catch (Exception e) {
                Log.e("MovieTask.onPostExecute", e.toString());
            }
        }
    }
}
