package com.joy.app.model;

import java.util.Date;

public class Attendance {
	
	String title;
	Date startDate;
	String start;
	String comment;
	long acadAreaMillis;
	
	public long getAcadAreaMillis() {
		return acadAreaMillis;
	}
	public void setAcadAreaMillis(long acadAreaMinutes) {
		this.acadAreaMillis = acadAreaMinutes;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return "Attendance [title=" + title + ", startDate=" + startDate
				+ ", start=" + start + ", comment=" + comment
				+ ", acadAreaMillis=" + acadAreaMillis + "]";
	}
	
	
	
}
