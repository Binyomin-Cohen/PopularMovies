package com.javaguy.seanc.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by seanc on 09/07/16.
 */
public class CustArrAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private Object[] objArray;

    public CustArrAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objArray = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View movieView = inflater.inflate(resource, parent, false);
        ImageView imageView = (ImageView)movieView.findViewById(R.id.imageView);
        try {
            JSONObject jsonObject = new JSONObject((String) objArray[position]);
            String url = "http://image.tmdb.org/t/p/w185/" + jsonObject.getString("poster_path");
            Picasso.with(context).load(url).into(imageView);
        }
        catch (Exception e){
            Log.e("CustArrAdapter.getView", e.toString());
        }
        return movieView;
    }

}
