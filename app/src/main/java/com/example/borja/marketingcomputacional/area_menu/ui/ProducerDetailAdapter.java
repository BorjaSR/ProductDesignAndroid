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
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 28/02/2016.
 */
public class ProducerDetailAdapter extends ArrayAdapter<Attribute> {

    private Context context;
    private int resource;
    private ArrayList<Attribute> attributes;

    public ProducerDetailAdapter(Context context, int resource, ArrayList<Attribute> attributes) {
        super(context, resource, attributes);
        this.context = context;
        this.resource = resource;
        this.attributes = attributes;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }

        Attribute attribute = attributes.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.valor_atributo);

        name.setText(attribute.getName() + ": Valor " + StoredData.Producer_selected.getProduct().getAttributeValue().get(attribute));

        return convertView;
    }
}