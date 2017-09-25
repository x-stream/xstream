package com.thoughtworks.xstream.mapper.newtest1;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("mapper")
public class Mapper {
	
	@XStreamImplicit(itemFieldName="resultMap")
	public List<ResultMap> resultMaps;

	@XStreamImplicit(itemFieldName="select")
	public List<Select> select;
	
	@XStreamImplicit(itemFieldName="insert")
	public List<Insert> insert;

	@XStreamImplicit(itemFieldName="update")
	public List<Update> update;

	@XStreamImplicit(itemFieldName="delete")
	public List<Delete> delete;

	public Mapper() {
		System.out.println("Mapper ctor here");
	}
	
	public ResultMap getResultMap(String id) {
		for(ResultMap rm: resultMaps) {
			if(rm.id.equals(id)) {
				return rm;
			}
		}
		return null;
	}
	
	public String toString() {
		String ret = "";
		if(this.resultMaps!=null) {
			for(ResultMap rm:this.resultMaps) {
				ret = ret + rm.toString();
			}
		}
		return ret;
	}
	
	public List<Select> getSelect() {
		return select;
	}

	public void setSelect(List<Select> select) {
		this.select = select;
	}

	public List<Insert> getInsert() {
		return insert;
	}

	public void setInsert(List<Insert> insert) {
		this.insert = insert;
	}

	public List<Update> getUpdate() {
		return update;
	}

	public void setUpdate(List<Update> update) {
		this.update = update;
	}

	public List<Delete> getDelete() {
		return delete;
	}

	public void setDelete(List<Delete> delete) {
		this.delete = delete;
	}

	
}
