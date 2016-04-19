package com.example.borja.marketingcomputacional.general;

/**
 * Created by Borja on 19/04/2016.
 */
public class LinkedAttribute {

    private Attribute attribute1;
    private int Value1;
    private Attribute attribute2;
    private int Value2;
    private int scoreModification;

    public LinkedAttribute() {
    }

    public Attribute getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(Attribute attribute1) {
        this.attribute1 = attribute1;
    }

    public int getValue1() {
        return Value1;
    }

    public void setValue1(int value1) {
        Value1 = value1;
    }

    public Attribute getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(Attribute attribute2) {
        this.attribute2 = attribute2;
    }

    public int getValue2() {
        return Value2;
    }

    public void setValue2(int value2) {
        Value2 = value2;
    }

    public int getScoreModification() {
        return scoreModification;
    }

    public void setScoreModification(int scoreModification) {
        this.scoreModification = scoreModification;
    }
}
