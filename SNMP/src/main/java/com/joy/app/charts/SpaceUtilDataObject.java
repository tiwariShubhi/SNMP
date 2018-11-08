package com.joy.app.charts;

/**
 * 
 * @author joy
 * DTO
 */
public class SpaceUtilDataObject {
	
	String name;
	int y;
	boolean drilldown = true;
	
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
	public boolean isDrilldown() {
		return drilldown;
	}
	public void setDrilldown(boolean drilldown) {
		this.drilldown = drilldown;
	}
	
	@Override
	public String toString() {
		return "SpaceUtilDataObject [name=" + name + ", y=" + y
				+ ", drilldown=" + drilldown + "]";
	}
	
}
