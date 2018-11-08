package com.joy.app.charts;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author joy
 * DTO
 */

public class SpaceUtilDrilldownMonthObject {
	
	String name;
	List<SpaceUtilDrilldownDataObject> data;
	
	public SpaceUtilDrilldownMonthObject() {
		
		this.name = null;
		this.data = new ArrayList<SpaceUtilDrilldownDataObject>();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SpaceUtilDrilldownDataObject> getData() {
		return data;
	}
	public void setData(List<SpaceUtilDrilldownDataObject> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SpaceUtilDrilldownMonthObject [name=" + name + ", data=" + data
				+ "]";
	}
	
	

}
