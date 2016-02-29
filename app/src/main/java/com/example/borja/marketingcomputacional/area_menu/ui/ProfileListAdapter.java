package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.CustomerProfile;
import com.example.borja.marketingcomputacional.R;

import java.util.ArrayList;

/**
 * Created by Borja on 28/02/2016.
 */
public class ProfileListAdapter extends ArrayAdapter<CustomerProfile> {

    private Context context;
    private int resource;
    private ArrayList<CustomerProfile> Profiles;

    public ProfileListAdapter(Context context, int resource, ArrayList<CustomerProfile> profiles) {
        super(context, resource, profiles);
        this.context = context;
        this.resource = resource;
        this.Profiles = profiles;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }

        CustomerProfile profile = Profiles.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.attribute_name);
        TextView number_values = (TextView) convertView.findViewById(R.id.attribute_number_values);

        name.setText("Customer profile " + (position + 1));

        return convertView;
    }
}