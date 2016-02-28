package com.example.borja.marketingcomputacional.main_activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.R;

/**
 * Created by Borja on 20/02/2016.
 */

public class MainViewPagerFragment extends Fragment {


    public TextView title_viewer;
    public ImageView image_viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.main_view_pager, container, false);

        title_viewer = (TextView) rootView.findViewById(R.id.title_view_pager);
        image_viewer = (ImageView) rootView.findViewById(R.id.image_view_pager);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        title_viewer.setTypeface(font);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().getInt("page") == 0) {
            title_viewer.setText("Algoritmo Genetico");
            image_viewer.setImageResource(R.drawable.adn);
        } else {
            title_viewer.setText("Minimax");
            image_viewer.setImageResource(R.drawable.minimax);
        }
    }

}
