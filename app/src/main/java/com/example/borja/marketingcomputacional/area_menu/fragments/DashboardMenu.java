package com.example.borja.marketingcomputacional.area_menu.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        LinearLayout percent_init_cust_mean_value_container = (LinearLayout) findViewById(R.id.percent_init_cust_mean_value_container);

        ImageView view_attributes = (ImageView)findViewById(R.id.view_attributes);
//        view_attributes.setTypeface(StoredData.roboto_regular(getApplicationContext()));

        ImageView view_profiles = (ImageView)findViewById(R.id.view_profiles);
//        view_profiles.setTypeface(StoredData.roboto_regular(getApplicationContext()));

        ImageView view_producers = (ImageView)findViewById(R.id.view_producers);
//        view_producers.setTypeface(StoredData.roboto_regular(getApplicationContext()));

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

        TextView mean = (TextView) findViewById(R.id.mean_value);
        TextView init_mean = (TextView) findViewById(R.id.init_mean_value);
        TextView std_dev = (TextView) findViewById(R.id.std_dev_value);
        TextView init_std_dev = (TextView) findViewById(R.id.init_std_dev_value);
        TextView cust_mean = (TextView) findViewById(R.id.cust_mean_value);
        TextView percent_cust = (TextView) findViewById(R.id.percent_cust_mean_value);
        TextView percent_init_cust = (TextView) findViewById(R.id.percent_init_cust_mean_value);
        TextView price = (TextView) findViewById(R.id.price_value);



        std_dev.setText(StoredData.stdDev + "");
        init_std_dev.setText(StoredData.initStdDev + "");
        cust_mean.setText(StoredData.custMean + "");
        percent_cust.setText(StoredData.percCust + "");

        if(StoredData.Fitness == StoredData.Customers){
            mean.setText(StoredData.mean + "");
            init_mean.setText(StoredData.initMean + "");
            percent_init_cust.setText(StoredData.initPercCust + "");
        }else if(StoredData.Fitness == StoredData.Benefits){
            mean.setText(StoredData.mean + " €");
            init_mean.setText(StoredData.initMean + " €");
            TextView percent_init_cust_mean_value_text = (TextView) findViewById(R.id.percent_init_cust_mean_value_text);
            percent_init_cust_mean_value_text.setText("% of increased benefits: ");
            percent_init_cust_mean_value_container.setVisibility(View.GONE);
        }

        if(StoredData.Algorithm == StoredData.GENETIC){
            price.setVisibility(View.VISIBLE);
            price.setText(StoredData.My_price + " €");
        }else{
            price.setVisibility(View.GONE);
        }
    }
}
