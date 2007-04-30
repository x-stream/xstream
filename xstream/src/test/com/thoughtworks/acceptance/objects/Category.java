package com.thoughtworks.acceptance.objects;

import java.util.List;

public class Category {
	
	String name;
	String id;
	List products;
	
	public Category(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getProducts() {
		return products;
	}

	public void setProducts(List products) {
		this.products = products;
	}
	
	public String toString() {
		String ret = "[" + name + ", " + id;
		if (products != null) {
			ret += "\n{";
            for (java.util.Iterator it = products.iterator(); it.hasNext();) {
                Product product = (Product) it.next();
                ret += product + "\n";
            }
            ret += "}";
		}
		ret += "]";
		return ret;
	}

}