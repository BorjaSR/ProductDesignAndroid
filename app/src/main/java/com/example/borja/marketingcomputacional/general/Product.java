package com.example.borja.marketingcomputacional.general;

import java.util.HashMap;

public class Product implements Cloneable{
	
	private HashMap<Attribute, Integer> attributeValue;
	private int Price;
	
	public Product() {
		super();
	}

	public Product(HashMap<Attribute, Integer> product) {
		super();
		attributeValue = product;
	}

	public HashMap<Attribute, Integer> getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(HashMap<Attribute, Integer> product) {
		attributeValue = product;
	}

	public int getPrice() {
		return Price;
	}

	public void setPrice(int price) {
		Price = price;
	}

	/**Creates a deep copy of Product*/
	public Product clone(){
		Product product = new Product(this.attributeValue);
		product.setPrice(this.Price);
		return product;
	}
}



