package com.javaguy.seanc.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MovieDetailActivity extends AppCompatActivity {

    TextView movieTitleView = null;
    TextView movieSummaryView = null;
    TextView releaseDateView = null;
    ImageView imageView = null;
    JSONObject movieJson = null;
    String videoUri = null;
    Button viewButton = null;
    Long movieId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movieTitleView= (TextView)findViewById(R.id.movieTitle);
        movieSummaryView = (TextView)findViewById(R.id.movieSummary);
        releaseDateView = (TextView)findViewById(R.id.releaseDate);
        imageView = (ImageView)findViewById(R.id.imageView2);
        viewButton = (Button)findViewById(R.id.button);

        Intent intent = getIntent();
        String movieDetails = intent.getStringExtra("MovieDetails");
        String title;
        String summary;
        String imageUrl;
        try {
             movieJson= new JSONObject(movieDetails);
            imageUrl = "http://image.tmdb.org/t/p/w185/" + movieJson.getString("poster_path");
            Picasso.with(this).load(imageUrl).into(imageView);
            movieTitleView.setText(movieJson.getString("title"));
            movieSummaryView.setText(movieJson.getString("overview"));
            releaseDateView.setText(movieJson.getString("release_date"));
            movieId = movieJson.getLong("id");

        } catch (JSONException e) {
            Log.e("MoveDetAct.onCreate", e.toString());
        }
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieUriTask muTask = new MovieUriTask();
                muTask.execute(movieId);
            }
        });

    }
    private class MovieUriTask extends AsyncTask<Long, Void, String>{


        @Override
        protected String doInBackground(Long... params) {
            String movieUrl = "https://api.themoviedb.org/3/movie/" + params[0] +"/videos?api_key=APIKEY";
            URL url = null;
            String key = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(movieUrl);
                connection = (HttpURLConnection) url.openConnection();
                Scanner scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";
                JSONObject jResult = new JSONObject(result);
                if( jResult.has("results") ){
                    JSONObject trailerObject = null;
                    JSONArray results = jResult.getJSONArray("results");
                    for( int i = 0; i < results.length(); ++i ){
                        if(results.getJSONObject(i).has("type") && "Trailer".equals( results.getJSONObject(i).getString("type") )){
                            trailerObject = results.getJSONObject(i);
                            break;
                        }
                    }
                    if(trailerObject !=null){
                         key = trailerObject.getString("key");
                    }
                }
            }
            catch (Exception e){
                Log.e("MDetailAsyncT", e.toString());
            }
            finally {
                connection.disconnect();
            }
            return key;
        }

        protected void onPostExecute( String key ){
            if(key != null){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( "http://www.youtube.com/watch?v="+key )));
            }
            else{
                Log.d("TheKey", "isNull");
            }

        }
    }
}
