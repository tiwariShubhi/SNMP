package com.joy.app.model;

import java.util.Date;

import org.joda.time.Period;

public class FilteredLog {
	
	Log log;
	Date startTime;
	Date endTime;
	String duration;
	
	
	public Log getLog() {
		return log;
	}
	public void setLog(Log log) {
		this.log = log;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "FilteredLog [oid=" + log.getOid() + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", duration=" + duration + "]";
	}
	
	
	
	
}
