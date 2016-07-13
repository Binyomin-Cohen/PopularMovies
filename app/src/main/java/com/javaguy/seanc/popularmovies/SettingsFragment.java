package com.javaguy.seanc.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFragment extends PreferenceFragment {
  public void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences);
  }
   @Override
  public void onActivityCreated(Bundle savedInstanceState){
      super.onActivityCreated(savedInstanceState);
       getView().setBackgroundColor(Color.LTGRAY);
  }

}
