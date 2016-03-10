package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_menu.ui.SubprofileAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardSubprofiles extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list);
        setTitle(StoredData.profile_name_selected);

        ListView profile_list = (ListView) findViewById(R.id.attribute_list);
        SubprofileAdapter adapter = new SubprofileAdapter(getApplicationContext(), R.layout.list_attribute_item, StoredData.profile_selected.getSubProfiles());
        profile_list.setAdapter(adapter);

        profile_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoredData.Subprofile_selected = StoredData.profile_selected.getSubProfiles().get(position);

                Intent dashboard_subprofile_details = new Intent(getApplicationContext(), DashboardSubprofilesDetail.class);
                dashboard_subprofile_details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(dashboard_subprofile_details);
            }
        });
    }
}
