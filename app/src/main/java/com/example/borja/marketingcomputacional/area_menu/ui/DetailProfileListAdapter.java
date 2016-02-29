package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.R;

import java.util.ArrayList;

/**
 * Created by Borja on 28/02/2016.
 */
public class DetailProfileListAdapter extends ArrayAdapter<Attribute> {

    private Context context;
    private int resource;
    private ArrayList<Attribute> Attributes;

    public DetailProfileListAdapter(Context context, int resource, ArrayList<Attribute> attributes) {
        super(context, resource, attributes);
        this.context = context;
        this.resource = resource;
        this.Attributes = attributes;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }

        Attribute attr = Attributes.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.attribute_name);
        TextView number_values = (TextView) convertView.findViewById(R.id.attribute_number_values);

        name.setText(attr.getName());
        number_values.setText("Valoraciones para el atributo X: " + attr.getMAX());

        return convertView;
    }
}