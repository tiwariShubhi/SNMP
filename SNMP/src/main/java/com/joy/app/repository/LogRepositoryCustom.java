package com.joy.app.repository;

import java.util.Date;
import java.util.List;

import org.joda.time.Period;

import com.joy.app.model.FootfallData;
import com.joy.app.model.Log;

public interface LogRepositoryCustom {

	public List<Log> findLatest50();
	
	
	
	public List<Log> findByHashedMac(String hash);
	
	public List<Log> findByHashedMacDuration(String hash, Date from, Date to);
	
	public Log firstInAcademicAreaToday(String hash);
	
	public Date avgAcademicAreaEntry(String hash);
	
	public Log exitAcademicArea(String hash);
	
	public Date avgAcademicAreaExit(String hash);
	
	//public float avgAcademicHours(String hash);
	
	public FootfallData footfallData(Date from, Date to);




	public List<Log> findByMultipleHashedMac(String hash1, String hash2);
	
	public List<Log> findByMultipleHashedMacDuration(String hash1, String hash2, Date from, Date to);

	public Log findAndUpdateLog(String hash, Date before, long DaySerial);
	
	public List<Log> findByHashedMacDurationEndTs(String hash, Date from, Date to);

	public List<Log> findByLocationDurationEndTsPeopleFilter(String place, Date from, Date to, int people);
	
	public Log findLastNullEndTsLog(String hash, Date before, long daySerial);
}
