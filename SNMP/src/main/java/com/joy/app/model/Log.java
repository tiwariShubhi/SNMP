package com.joy.app.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Log {
	
	@Id
	private String id;
	private String oid;
	private Date ts;
	private Date endTs;
	private long daySerialNo;
	
	private String bsnStationAPMacAddr;
	private String bsnUserIpAddress;
	private String bsnStationMacAddress;
	private String bsnAPName;
	private String cldcClientMacAddress;
	private String cldcApMacAddress;
	private String cLApName;
	private String clrRrmNeighborApCount;
	private String clrRrmNeighborApMacAddress;
	private String clrRrmNeighborApRssi;
	private String clrRrmRssiHistogramValues;
	private String cldcClientSSID;
	private String cldcClientSessionID;
	private String cLApSysMacAddress;
	private String cLSiIdrDeviceId;
	private String cLSiIdrDeviceType;
	private String cLSiIdrAffectedChannels;
	private String cLSiIdrClusterId;
	private String cLSiAlarmClear;
	private String cLSiIdrPreviousClusterId;
	private String cldcClientIPAddress;
	
	
	
	public Log(){
		
	}
	
	public Log(String _oid, Date _ts){
		this.oid = _oid;
		this.ts = _ts;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public long getDaySerialNo() {
		return daySerialNo;
	}

	public void setDaySerialNo(long daySerialNo) {
		this.daySerialNo = daySerialNo;
	}
	
	public String getBsnStationAPMacAddr() {
		return bsnStationAPMacAddr;
	}

	public void setBsnStationAPMacAddr(String bsnStationAPMacAddr) {
		this.bsnStationAPMacAddr = bsnStationAPMacAddr;
	}

	public String getBsnUserIpAddress() {
		return bsnUserIpAddress;
	}

	public void setBsnUserIpAddress(String bsnUserIpAddress) {
		this.bsnUserIpAddress = bsnUserIpAddress;
	}

	public String getBsnStationMacAddress() {
		return bsnStationMacAddress;
	}

	public void setBsnStationMacAddress(String bsnStationMacAddress) {
		this.bsnStationMacAddress = bsnStationMacAddress;
	}

	public String getBsnAPName() {
		return bsnAPName;
	}

	public void setBsnAPName(String bsnAPName) {
		this.bsnAPName = bsnAPName;
	}

	public String getCldcClientMacAddress() {
		return cldcClientMacAddress;
	}

	public void setCldcClientMacAddress(String cldcClientMacAddress) {
		this.cldcClientMacAddress = cldcClientMacAddress;
	}

	public String getCldcApMacAddress() {
		return cldcApMacAddress;
	}

	public void setCldcApMacAddress(String cldcApMacAddress) {
		this.cldcApMacAddress = cldcApMacAddress;
	}

	public String getcLApName() {
		return cLApName;
	}

	public void setcLApName(String cLApName) {
		this.cLApName = cLApName;
	}

	public String getClrRrmNeighborApCount() {
		return clrRrmNeighborApCount;
	}

	public void setClrRrmNeighborApCount(String clrRrmNeighborApCount) {
		this.clrRrmNeighborApCount = clrRrmNeighborApCount;
	}

	public String getClrRrmNeighborApMacAddress() {
		return clrRrmNeighborApMacAddress;
	}

	public void setClrRrmNeighborApMacAddress(String clrRrmNeighborApMacAddress) {
		this.clrRrmNeighborApMacAddress = clrRrmNeighborApMacAddress;
	}

	public String getClrRrmNeighborApRssi() {
		return clrRrmNeighborApRssi;
	}

	public void setClrRrmNeighborApRssi(String clrRrmNeighborApRssi) {
		this.clrRrmNeighborApRssi = clrRrmNeighborApRssi;
	}

	public String getClrRrmRssiHistogramValues() {
		return clrRrmRssiHistogramValues;
	}

	public void setClrRrmRssiHistogramValues(String clrRrmRssiHistogramValues) {
		this.clrRrmRssiHistogramValues = clrRrmRssiHistogramValues;
	}

	public String getCldcClientSSID() {
		return cldcClientSSID;
	}

	public void setCldcClientSSID(String cldcClientSSID) {
		this.cldcClientSSID = cldcClientSSID;
	}

	public String getCldcClientSessionID() {
		return cldcClientSessionID;
	}

	public void setCldcClientSessionID(String cldcClientSessionID) {
		this.cldcClientSessionID = cldcClientSessionID;
	}

	public String getcLApSysMacAddress() {
		return cLApSysMacAddress;
	}

	public void setcLApSysMacAddress(String cLApSysMacAddress) {
		this.cLApSysMacAddress = cLApSysMacAddress;
	}

	public String getcLSiIdrDeviceId() {
		return cLSiIdrDeviceId;
	}

	public void setcLSiIdrDeviceId(String cLSiIdrDeviceId) {
		this.cLSiIdrDeviceId = cLSiIdrDeviceId;
	}

	public String getcLSiIdrDeviceType() {
		return cLSiIdrDeviceType;
	}

	public void setcLSiIdrDeviceType(String cLSiIdrDeviceType) {
		this.cLSiIdrDeviceType = cLSiIdrDeviceType;
	}

	public String getcLSiIdrAffectedChannels() {
		return cLSiIdrAffectedChannels;
	}

	public void setcLSiIdrAffectedChannels(String cLSiIdrAffectedChannels) {
		this.cLSiIdrAffectedChannels = cLSiIdrAffectedChannels;
	}

	public String getcLSiIdrClusterId() {
		return cLSiIdrClusterId;
	}

	public void setcLSiIdrClusterId(String cLSiIdrClusterId) {
		this.cLSiIdrClusterId = cLSiIdrClusterId;
	}

	public String getcLSiAlarmClear() {
		return cLSiAlarmClear;
	}

	public void setcLSiAlarmClear(String cLSiAlarmClear) {
		this.cLSiAlarmClear = cLSiAlarmClear;
	}

	public String getcLSiIdrPreviousClusterId() {
		return cLSiIdrPreviousClusterId;
	}

	public void setcLSiIdrPreviousClusterId(String cLSiIdrPreviousClusterId) {
		this.cLSiIdrPreviousClusterId = cLSiIdrPreviousClusterId;
	}

	public String getCldcClientIPAddress() {
		return cldcClientIPAddress;
	}

	public void setCldcClientIPAddress(String cldcClientIPAddress) {
		this.cldcClientIPAddress = cldcClientIPAddress;
	}

	public String getOid() {
		return oid;
	}
	
	public Date getTs() {
		return ts;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public void setTs(Date ts) {
		this.ts = ts;
	}
	
	public Date getEndTs() {
		return endTs;
	}

	public void setEndTs(Date endTs) {
		this.endTs = endTs;
	}


	public String giveMACAddress(){
		if(this.cldcClientMacAddress != null)
			return this.cldcClientMacAddress;
		else if(this.bsnStationMacAddress != null)
			return this.bsnStationMacAddress;
		
		return null;
	}
	
	public String giveAPName(){
		if(this.cLApName != null)
			return this.cLApName;
		else if(this.bsnAPName != null)
			return this.bsnAPName;
		
		return null;
	}
	
	public String giveAPNameOrDisconnected(){
		
		if(this.cLApName != null)
			return this.cLApName;
		else 
			return "Disconnected"; 
	}
	

	
	
	@Override

	    public String toString() {
	        return String.format(
	                "Log[oid=%s, ts='%s', endts='%s',dayserial='%d%n', cLApName='%s', MAC='%s', MAC='%s' ]",
	                oid, ts, endTs, daySerialNo,cLApName, cldcClientMacAddress, bsnStationMacAddress);
	    }

	public String toJson(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
