package com.thoughtworks.xstream.mapper.newtest1;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("resultMap")
public class ResultMap {
	
	@XStreamAlias("id")
	@XStreamAsAttribute
	public String id;

	@XStreamAlias("type")
	@XStreamAsAttribute
	public String type;
	
	
	@XStreamAlias("id")
	Id id1;
	
	
	@XStreamImplicit(itemFieldName="result")
	List<Result> results;
	

	public ResultMap() {
		System.out.println("resultMap ctor here");
	}
	
	public String toString() {
		String ret = id + " " + type + " \n";
		ret = ret + " " + id1 + "\n";
		if(results!=null) {
			for(int i=0;i<results.size();i++) {
				ret = ret + results.get(i).toString();
			}
		}
		return ret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Id getId1() {
		return id1;
	}

	public void setId1(Id id1) {
		this.id1 = id1;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	
	
}
