package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;
import com.example.borja.marketingcomputacional.area_menu.ui.AttributeListAdapter;

/**
 * Created by Borja on 28/02/2016.
 */
public class DashboardAttributes extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_data);

        ListView attribute_list = (ListView) findViewById(R.id.attribute_list);
        AttributeListAdapter adapter = new AttributeListAdapter(getApplicationContext(), R.layout.list_attribute_item, StoredData.Atributos);
        attribute_list.setAdapter(adapter);
    }
}
