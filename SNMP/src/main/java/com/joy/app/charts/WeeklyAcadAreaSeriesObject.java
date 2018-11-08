package com.joy.app.charts;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author joy
 * DTO
 */
public class WeeklyAcadAreaSeriesObject {
	
	private String name;
	private List<WeeklyAcadAreaDataObject> data = new ArrayList<WeeklyAcadAreaDataObject>();
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<WeeklyAcadAreaDataObject> getData() {
		return data;
	}
	public void setData(List<WeeklyAcadAreaDataObject> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "WeeklyAcadAreaSeriesObject [name=" + name + ", data=" + data
				+ "]";
	}
	
	
	
}
