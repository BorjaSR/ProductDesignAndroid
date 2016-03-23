package com.example.borja.marketingcomputacional.area_menu.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.CustomerProfile;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardProfilesDetail;
import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardSubprofiles;
import com.example.borja.marketingcomputacional.general.StoredData;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater1.inflate(resource, null);
        }

        final CustomerProfile profile = Profiles.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.attribute_name);
        TextView view_attribute_values = (TextView) convertView.findViewById(R.id.view_attribute_values);
        TextView view_subprofiles = (TextView) convertView.findViewById(R.id.view_subprofiles);

        name.setText("Customer profile " + (position + 1));

        if(StoredData.Algorithm.equals(StoredData.MINIMAX))
            view_subprofiles.setVisibility(View.GONE);
        else
            view_subprofiles.setVisibility(View.VISIBLE);

        view_attribute_values.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoredData.profile_name_selected = "Customer profile " + (position + 1);
                StoredData.Attributes_profile_selected = profile.getScoreAttributes();

                Intent dashboard_detail_profiles = new Intent(context, DashboardProfilesDetail.class);
                dashboard_detail_profiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dashboard_detail_profiles);
            }
        });

        view_subprofiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoredData.profile_name_selected = "Customer profile " + (position + 1);
                StoredData.profile_selected = profile;

                Intent dashboard_subprofiles = new Intent(context, DashboardSubprofiles.class);
                dashboard_subprofiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dashboard_subprofiles);
            }
        });
        return convertView;
    }
}