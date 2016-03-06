package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 28/02/2016.
 */
public class SubprofileListAdapter extends ArrayAdapter<SubProfile> {

    private Context context;
    private int resource;
    private ArrayList<SubProfile> Subprofiles;

    public SubprofileListAdapter(Context context, int resource, ArrayList<SubProfile> subprofiles) {
        super(context, resource, subprofiles);
        this.context = context;
        this.resource = resource;
        this.Subprofiles = subprofiles;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }


        SubProfile subprofile = Subprofiles.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.attribute_name);
        TextView number_values = (TextView) convertView.findViewById(R.id.attribute_number_values);

        name.setText(subprofile.getName());
        number_values.setText("El valor 1 elegido es el " + subprofile.getValueChosen().get(StoredData.Atributos.get(0)));

        return convertView;
    }
}