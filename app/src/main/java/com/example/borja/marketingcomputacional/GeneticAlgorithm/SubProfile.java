package com.example.borja.marketingcomputacional.GeneticAlgorithm;

import com.example.borja.marketingcomputacional.general.Attribute;

import java.util.HashMap;

/**
 * Created by Borja on 03/03/2016.
 */
public class SubProfile {

    private String name;
    private HashMap<Attribute, Integer> ValueChosen;

    public SubProfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Attribute, Integer> getValueChosen() {
        return ValueChosen;
    }

    public void setValueChosen(HashMap<Attribute, Integer> valueChosen) {
        ValueChosen = valueChosen;
    }
}
