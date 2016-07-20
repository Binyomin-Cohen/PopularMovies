package com.javaguy.seanc.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by seanc on 20/07/16.
 */
public class ReviewsAdapter extends ArrayAdapter {

    private Context context;
    private int resourceId;
    private Object[] objs;

    public ReviewsAdapter(Context context, int resource, Object[] objs){
        super(context, resource, objs);
        this.context = context;
        this.resourceId = resource;
        this.objs = objs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View reviewView = layoutInflater.inflate(resourceId, parent, false);
        try {
            JSONObject json = new JSONObject((String) objs[position]);
            String author = json.getString("author");
            String content = json.getString("content");
            TextView authorView = (TextView)reviewView.findViewById(R.id.authorTextView);
            TextView contentView = (TextView)reviewView.findViewById(R.id.contentTextView);
            authorView.setText(author);
            contentView.setText(content);

        }
        catch(Exception e){
            Log.e("reviewsadapter", e.toString());
        }
        return reviewView;
    }
}
