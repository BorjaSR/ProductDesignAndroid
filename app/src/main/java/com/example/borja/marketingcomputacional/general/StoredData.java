package com.example.borja.marketingcomputacional.general;

import android.content.Context;
import android.graphics.Typeface;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.CustomerProfile;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.Producer;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.main_activity.MainActivity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Borja on 21/02/2016.
 */
public class StoredData {


    public static final String GENETIC = "GeneticAlgorithm";
    public static final String MINIMAX = "MinimaxAlgorithm";
    public static com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm GeneticAlgorithm;
    public static com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax Minimax;

    public static ArrayList<Attribute> Atributos;
    public static ArrayList<CustomerProfile> Profiles;
    public static ArrayList<Producer> Producers;

    public static ArrayList<Attribute> Attributes_profile_selected;
    public static CustomerProfile profile_selected;
    public static String profile_name_selected;
    public static SubProfile Subprofile_selected;
    public static Producer Producer_selected;

//    FINAL DATA
    public static double mean = -1;
    public static double initMean = -1;
    public static double stdDev = -1;
    public static double initStdDev = -1;
    public static int custMean = -1;
    public static double percCust = -1;
    public static double initPercCust = -1;
    public static String Algorithm;

    public static Typeface roboto_light(Context context){
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
    }

    public static Typeface roboto_regular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }

    public static Typeface roboto_medium(Context context){
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
    }
}
