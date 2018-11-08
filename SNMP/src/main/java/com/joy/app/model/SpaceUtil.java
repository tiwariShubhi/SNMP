package com.joy.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.joy.app.charts.SpaceUtilDrilldownObject;
import com.joy.app.charts.SpaceUtilSeriesObject;

public class SpaceUtil {
	
	
	String place;
	Date from;
	Date to;
	int people;
	
	
	List<SpaceUtilSeriesObject> series =  new ArrayList<SpaceUtilSeriesObject>();
	SpaceUtilDrilldownObject drilldown ;
	
	public SpaceUtil() {
		
	}
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date lastUpdated) {
		this.to = lastUpdated;
	}
	public List<SpaceUtilSeriesObject> getSeries() {
		return series;
	}
	public void setSeries(List<SpaceUtilSeriesObject> series) {
		this.series = series;
	}
	public SpaceUtilDrilldownObject getDrilldown() {
		return drilldown;
	}
	public void setDrilldown(SpaceUtilDrilldownObject drilldown) {
		this.drilldown = drilldown;
	}
	
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	
	@Override
	public String toString() {
		return "SpaceUtil [place=" + place + ", from=" + from + ", to=" + to
				+ ", people=" + people + ", series=" + series + ", drilldown="
				+ drilldown + "]";
	}
	
	
	
	
	
}
