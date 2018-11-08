package com.joy.app.charts;


/**
 * 
 * @author joy
 * DTO
 */
public class SpaceUtilDrilldownDataObject {
	
	String name;
	int y;
	
	
	public SpaceUtilDrilldownDataObject() {
		
	}
	public SpaceUtilDrilldownDataObject(String name) {
		super();
		this.name = name;
		this.y = 0;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "SpaceUtilDrilldownDataObject [name=" + name + ", y=" + y + "]";
	}
	
	
}
