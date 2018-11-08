package com.joy.app.charts;

import java.util.Date;

/**
 * 
 * @author joy
 * DTO
 */

public class WeeklyAcadAreaDataObject {

	private int x;
	private Date low;
	private Date high;
	
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public Date getLow() {
		return low;
	}
	public void setLow(Date low) {
		this.low = low;
	}
	public Date getHigh() {
		return high;
	}
	public void setHigh(Date high) {
		this.high = high;
	}
	
	@Override
	public String toString() {
		return "WeeklyAcadAreaDataObject [x=" + x + ", low=" + low + ", high=" + high + "]";
	}
	
}
