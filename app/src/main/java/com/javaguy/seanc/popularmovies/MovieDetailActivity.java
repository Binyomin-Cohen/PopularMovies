package com.javaguy.seanc.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    TextView movieTitleView = null;
    TextView movieSummaryView = null;
    ImageView imageView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movieTitleView= (TextView)findViewById(R.id.originalTitle);
        movieSummaryView = (TextView)findViewById(R.id.movieSummary);
        imageView = (ImageView)findViewById(R.id.imageView2);
        Intent intent = getIntent();
        String movieDetails = intent.getStringExtra("MovieDetails");
        String title;
        String summary;
        String imageUrl;
        try {
            JSONObject movieJson = new JSONObject(movieDetails);
            imageUrl = "http://image.tmdb.org/t/p/w185/" + movieJson.getString("poster_path");
            Picasso.with(this).load(imageUrl).into(imageView);
            movieTitleView.setText(movieJson.getString("title"));
            movieSummaryView.setText(movieJson.getString("overview"));
        } catch (JSONException e) {
            Log.e("MoveDetAct.onCreate", e.toString());
        }
    }
}
