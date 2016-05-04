package com.example.borja.marketingcomputacional.ParticleSwarmOptimization;

import android.content.Context;
import android.content.Intent;

import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardMenu;
import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 04/05/2016.
 */
public class ParticleSwarmOptimization {

    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();
    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

    public void start(Context context) {

        startPSO();

        Intent dashboard_menu = new Intent(context, DashboardMenu.class);
        dashboard_menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dashboard_menu);
    }

    private void startPSO() {
        generateImput();
    }

    private void generateImput() {
        TotalAttributes = StoredData.Atributos;
        CustomerProfiles = StoredData.Profiles;
        Producers = StoredData.Producers;
    }
}
