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
import com.example.borja.marketingcomputacional.area_menu.ui.ProducerAdapter;
import com.example.borja.marketingcomputacional.area_menu.ui.ProfileListAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardProducers extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list);

        ListView producers_list = (ListView) findViewById(R.id.attribute_list);
        ProducerAdapter adapter = new ProducerAdapter(getApplicationContext(), R.layout.list_attribute_item, StoredData.Producers);
        producers_list.setAdapter(adapter);

        producers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StoredData.Producer_selected = StoredData.Producers.get(position);

                Intent dashboard_producers = new Intent(getApplicationContext(), DashboardProducersDetail.class);
                dashboard_producers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(dashboard_producers);
            }
        });
    }
}
