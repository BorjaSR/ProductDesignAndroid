package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_menu.ui.ProfileListAdapter;
import com.example.borja.marketingcomputacional.area_menu.ui.SubprofileListAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardSubprofiles extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_data);
        setTitle(StoredData.profile_name_selected);

        ListView profile_list = (ListView) findViewById(R.id.attribute_list);
        SubprofileListAdapter adapter = new SubprofileListAdapter(getApplicationContext(), R.layout.list_attribute_item, StoredData.profile_selected.getSubProfiles());
        profile_list.setAdapter(adapter);
    }
}
