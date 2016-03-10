package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_menu.ui.ProducerAdapter;
import com.example.borja.marketingcomputacional.area_menu.ui.ProducerDetailAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardProducersDetail extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.producers_detail);
        setTitle(StoredData.Producer_selected.getName());

        ListView profile_list = (ListView) findViewById(R.id.producer_detail_list);
        ProducerDetailAdapter adapter = new ProducerDetailAdapter(getApplicationContext(), R.layout.list_producer_detail_item, StoredData.Atributos);
        profile_list.setAdapter(adapter);
    }
}
