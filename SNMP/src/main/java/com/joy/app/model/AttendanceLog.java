package com.joy.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class AttendanceLog {
	
	@Id
	String hmac;
	List<Attendance> attendList = new ArrayList<Attendance>();
	Date validFrom;
	Date validUpto;
	
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	public List<Attendance> getAttendList() {
		return attendList;
	}
	public void setAttendList(List<Attendance> attendList) {
		this.attendList = attendList;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidUpto() {
		return validUpto;
	}
	public void setValidUpto(Date validUpto) {
		this.validUpto = validUpto;
	}
	
	@Override
	public String toString() {
		return "AttendanceLog [hmac=" + hmac + ", attendList=" + attendList
				+ ", validFrom=" + validFrom + ", validUpto=" + validUpto + "]";
	}
	
	
}
