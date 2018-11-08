package com.joy.app.cronJobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProgressUpdate {

	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 * displays progress of reading logs from NMS server at the eclipse's log
	 * 
	 */
	public void displayProgress(){
		
		log.info("Progress");
		System.out.println(Background.progressDate);
		
	}
	
}
