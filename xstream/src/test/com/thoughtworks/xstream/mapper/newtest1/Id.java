package com.thoughtworks.xstream.mapper.newtest1;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("id")
public class Id {
	@XStreamAlias("column")
	@XStreamAsAttribute
	public String column;
	
	@XStreamAlias("property")
	@XStreamAsAttribute	
	public String property;
	
	public String toString() {
		return this.column + " " + this.property;
	}
	
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}
