package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_menu.ui.DetailProfileListAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */

public class DashboardProfilesDetail extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list);
        setTitle(StoredData.profile_name_selected);

        ListView detail_profile_list = (ListView) findViewById(R.id.attribute_list);
        DetailProfileListAdapter adapter = new DetailProfileListAdapter(getApplicationContext(), R.layout.list_detail_profile_item, StoredData.Attributes_profile_selected);
        detail_profile_list.setAdapter(adapter);
    }
}
