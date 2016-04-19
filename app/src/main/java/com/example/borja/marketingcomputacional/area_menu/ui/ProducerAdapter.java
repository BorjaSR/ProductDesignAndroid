package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.R;

import java.util.ArrayList;

/**
 * Created by Borja on 28/02/2016.
 */
public class ProducerAdapter extends ArrayAdapter<Producer> {

    private Context context;
    private int resource;
    private ArrayList<Producer> Producers;

    public ProducerAdapter(Context context, int resource, ArrayList<Producer> producers) {
        super(context, resource, producers);
        this.context = context;
        this.resource = resource;
        this.Producers = producers;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }

        Producer producer = Producers.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.attribute_name);
        TextView description = (TextView) convertView.findViewById(R.id.attribute_number_values);

        name.setText(producer.getName());
        description.setText("Pulsa para ver detalles");

        return convertView;
    }
}