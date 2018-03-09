package com.gheewala.recipekeeper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hetag on 12/10/2017.
 */

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.about_fragment,container, false);
        return view;
    }
}
