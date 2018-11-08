package com.joy.app.charts;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author joy
 * DTO
 */

public class SpaceUtilSeriesObject {

	String name;
	List<SpaceUtilDataObject> data;
		
	public SpaceUtilSeriesObject() {
		
		this.name = null;
		this.data = new ArrayList<SpaceUtilDataObject>();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SpaceUtilDataObject> getData() {
		return data;
	}
	public void setData(List<SpaceUtilDataObject> data) {
		this.data = data;
	}
	
	
	@Override
	public String toString() {
		return "SpaceUtilSeriesObject [name=" + name  + ", data="
				+ data + "]";
	}
	
	
	
}
