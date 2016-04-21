package com.example.borja.marketingcomputacional.main_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

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

        title_viewer.setTypeface(StoredData.roboto_light(getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (getArguments().getInt("page")){
            case 0:
                title_viewer.setText("Algoritmo Genético");
                image_viewer.setImageResource(R.drawable.adn);
                break;
            case 1:
                title_viewer.setText("Minimax");
                image_viewer.setImageResource(R.drawable.minimax);
                break;
        }
    }

}
