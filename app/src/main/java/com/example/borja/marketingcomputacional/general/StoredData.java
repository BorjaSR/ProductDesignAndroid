package com.example.borja.marketingcomputacional.general;

import android.content.Context;
import android.graphics.Typeface;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithmOLD;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;

import java.util.ArrayList;

/**
 * Created by Borja on 21/02/2016.
 */
public class StoredData {


    public static final int GENETIC = 0;
    public static final int MINIMAX = 1;
    public static final int PSO = 2;
    public static final int SA = 3;

    public static GeneticAlgorithmOLD GeneticAlgorithm;

//    INPUT DATA
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
    public static String meanString = "";
    public static double initMean = -1;
    public static String initMeanString = "";
    public static double stdDev = -1;
    public static String stdDevString = "";
    public static double initStdDev = -1;
    public static String initStdDevString = "";
    public static int custMean = -1;
    public static double percCust = -1;
    public static String percCustString = "";
    public static double initPercCust = -1;
    public static String initPercCustString = "";
    public static int Algorithm;
    public static int My_price;
    public static String My_priceString;

    public static int Customers = 0;
    public static int Benefits = 1;
    public static int Fitness = Customers;

    public static boolean isAttributesLinked = false;

    public static int number_Products = 1;


    public static final double VEL_LOW = -2;
    public static final double VEL_HIGH = 2;

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
