package com.example.borja.marketingcomputacional.GeneticAlgorithm;

import java.util.ArrayList;

public class CustomerProfile {
	
	private ArrayList<Attribute> scoreAttributes;

	public CustomerProfile(ArrayList<Attribute> scoreAttributes) {
		super();
		this.scoreAttributes = scoreAttributes;
	}

	public ArrayList<Attribute> getScoreAttributes() {
		return scoreAttributes;
	}

	public void setScoreAttributes(ArrayList<Attribute> scoreAttributes) {
		this.scoreAttributes = scoreAttributes;
	}
	
}

