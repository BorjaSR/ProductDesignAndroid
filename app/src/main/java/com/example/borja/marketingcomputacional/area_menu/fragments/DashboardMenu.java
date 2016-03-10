package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardMenu extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_menu);

        TextView view_attributes = (TextView)findViewById(R.id.view_attributes);
        view_attributes.setTypeface(StoredData.roboto_regular(getApplicationContext()));

        TextView view_profiles = (TextView)findViewById(R.id.view_profiles);
        view_profiles.setTypeface(StoredData.roboto_regular(getApplicationContext()));

        TextView view_producers = (TextView)findViewById(R.id.view_producers);
        view_producers.setTypeface(StoredData.roboto_regular(getApplicationContext()));

        view_attributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard_attributes = new Intent(getApplicationContext(), DashboardAttributes.class);
                dashboard_attributes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(dashboard_attributes);
            }
        });

        view_profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard_profiles = new Intent(getApplicationContext(), DashboardProfiles.class);
                dashboard_profiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(dashboard_profiles);
            }
        });

        view_producers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard_producers = new Intent(getApplicationContext(), DashboardProducers.class);
                dashboard_producers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(dashboard_producers);
            }
        });
    }
}
