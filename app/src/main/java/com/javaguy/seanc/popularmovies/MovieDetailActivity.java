package com.javaguy.seanc.popularmovies;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    Context context = this;
    ListView reviewsView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movieTitleView= (TextView)findViewById(R.id.movieTitle);
        movieSummaryView = (TextView)findViewById(R.id.movieSummary);
        releaseDateView = (TextView)findViewById(R.id.releaseDate);
        imageView = (ImageView)findViewById(R.id.imageView2);
        viewButton = (Button)findViewById(R.id.button);
        reviewsView = (ListView)findViewById(R.id.reviewsListView);

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
            ReviewsTask rt = new ReviewsTask();
            rt.execute(movieId);

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
            String movieUrl = "https://api.themoviedb.org/3/movie/" + params[0] +"/videos?api_key="+ MainActivity.API_KEY;
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

    private class ReviewsTask extends AsyncTask<Long, Void, String>{

        @Override
        protected String doInBackground(Long... params) {
            String theUrl = "https://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=" + MainActivity.API_KEY;
            HttpURLConnection connection = null;
            URL url = null;
            String result = null;
            try{
                url = new URL(theUrl);
                connection = (HttpURLConnection)url.openConnection();
                Scanner scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
                result = scanner.hasNext() ? scanner.next() : "";
            }
            catch(Exception e){
                Log.e("ReviewsTask", e.toString());
            }
            finally{
                connection.disconnect();
            }
            return result;
        }

        protected void onPostExecute(String reviews){
            try{
                JSONObject jReviews = new JSONObject(reviews);
                if( jReviews.has("results") && jReviews.getJSONArray("results").length() > 0){
                    JSONArray reviewsArray = jReviews.getJSONArray("results");
                    ArrayList<String> reviewsList = new ArrayList<String>();
                    for( int i = 0; i < reviewsArray.length(); ++i ){
                        JSONObject review = reviewsArray.getJSONObject(i);
                        JSONObject authCont;
                        if(review.has("author") && review.has("content")){
                            authCont = new JSONObject().put("author", review.getString("author")).put("content", review.getString("content"));
                            reviewsList.add(authCont.toString());
                        }
                    }
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(context, R.layout.movie_reviews, reviewsList.toArray());
                    reviewsView.setAdapter(reviewsAdapter);
                }

            }
            catch(Exception e){
                Log.e("RevTaskOnPoE", e.toString());
            }
        }
    }
}
