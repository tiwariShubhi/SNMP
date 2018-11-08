package com.joy.app.cronJobs;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.joy.app.repository.LogRepository;

@Component
public class Scheduler {
	
	public static final String path = "/var/log/";
	public static String fileName = "snmptraps.log";
	public static Boolean tail = false;
	//public static LocalDate date = LocalDate.now();
	public static LocalDate date = LocalDate.of(2018, Month.OCTOBER, 24);
	
	@Autowired
	Background background;
	
	@Autowired
	ProgressUpdate progressUpdate;
	
	@Autowired
	SpaceUtilDBUpdate spaceUtilDBUpdate;
	
	
	/**
	 * caller function for parsing logs 
	 * scheduled to start parsing for each day
	 * fixedDelay ensures second instance of the function starts after 5 seconds
	 */
	@Scheduled(initialDelay=1000, fixedDelay = 5000)
	
	public void parser(){
		
		try {
			System.out.println("Calling parser for "+Scheduler.date);
			background.parseLogs();
			System.out.println("Exiting parser for "+Scheduler.date);
		}catch (OutOfMemoryError e){
			e.printStackTrace();
			Scheduler.date = Scheduler.date.plusDays(1);
			return;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	
		
	}
	/**
	 * every 15 minutes displays the progress of reading logs from the NMS server 
	 * displays progress on the eclipse's log
	 */
	@Scheduled(initialDelay=3000, fixedDelay = 900000)
	
	public void progress(){
		progressUpdate.displayProgress();
	}
	
	/**
	 * runs every midnight 
	 * updates the space utilization DB with new data from the day
	 */
	
	@Scheduled(cron = "0 1 0 * * ?")
	
		public void spaceUtilChartUpdate(){
			spaceUtilDBUpdate.UpdateDB();
		}
		
	
}
