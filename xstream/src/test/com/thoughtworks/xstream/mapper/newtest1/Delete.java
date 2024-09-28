package com.thoughtworks.xstream.mapper.newtest1;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("delete")
public class Delete {
	@XStreamAlias("id")
	@XStreamAsAttribute
	public String id;

	@XStreamAlias("parameterType")
	@XStreamAsAttribute
	public String parameterType;

	
	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	
}
