package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.general.Attribute;
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

    @TargetApi(Build.VERSION_CODES.M)
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
        name.setText(attr.getName());

        LinearLayout valoration_content = (LinearLayout)convertView.findViewById(R.id.valorations_content);
        valoration_content.removeAllViews();
        for(int i = 0; i < attr.getScoreValues().size(); i++){
            TextView valoration = new TextView(context);
            valoration.setText("Puntuacion para el valor " + (i + 1) + ": " + attr.getScoreValues().get(i));
            valoration.setTextColor(context.getResources().getColor(R.color.grey_medium));
            valoration.setTextSize(15);
            valoration.setPadding(0, 0, 0, 5);
            valoration_content.addView(valoration);
        }

        return convertView;
    }
}