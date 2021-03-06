package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;
import com.example.borja.marketingcomputacional.area_menu.ui.ProfileListAdapter;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardProfiles extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list);

        ListView profile_list = (ListView) findViewById(R.id.attribute_list);
        ProfileListAdapter adapter = new ProfileListAdapter(getApplicationContext(), R.layout.list_profile_item, StoredData.Profiles);
        profile_list.setAdapter(adapter);
    }
}
