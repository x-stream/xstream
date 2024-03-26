package com.thoughtworks.xstream.mapper.newtest1;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("select")
public class Select {
	
	@XStreamAlias("id")
	@XStreamAsAttribute
	public String id;

	
	@XStreamAlias("parameterType")
	@XStreamAsAttribute
	public String parameterType;

	@XStreamAlias("resultType")
	@XStreamAsAttribute
	public String resultType;

	@XStreamAlias("resultMap")
	@XStreamAsAttribute
	public String resultMap;
	
	

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getResultMap() {
		return resultMap;
	}

	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
