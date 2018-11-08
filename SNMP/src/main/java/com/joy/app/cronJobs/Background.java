package com.joy.app.cronJobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.joy.app.model.Log;
import com.joy.app.repository.LogRepository;



@Component
public class Background{
	
	
	
	public static String progressDate;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss");
	
	private ChannelExec channelExec;
	private boolean flag;
	private long daySerial;
	private Date savedDate;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LogRepository logRepository;
	
	/**
	 * 
	 * @param currDate
	 * 
	 * updates the serial number of the logs 
	 * starts from 0 everyday 
	 */
	
	public void updateDaySerial(Date currDate){
    	
		if(savedDate == null){
    		savedDate = currDate;
    		daySerial =0;
    	}
    	else {
    		
    		if (currDate.getYear() == savedDate.getYear() 
    				&& currDate.getMonth() == savedDate.getMonth()
    				&& currDate.getDate() == savedDate.getDate())
    		{
    			daySerial++;
    		}
    		else{
    			
    			daySerial=0;
    			savedDate = currDate;
    		}
    	}
    }
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * to read previous logs from NMS server
	 * 
	 * if the system was not live for few days it is used to read missed data from NMS server
	 * 
	 * takes input starting date from Scheduler.java
	 */
	
	public void sftp() throws Exception{
		
		Session session = null;
		ChannelSftp sftpChannel = null;
		BufferedReader br = null;
		try{
			
		JSch jsch = new JSch();
		session = jsch.getSession("iiitd", "192.168.1.74", 22);
		
		session.setPassword("iiitd@456");
		session.setConfig("StrictHostKeyChecking", "no");
		log.info("Establishing Connection...");
		System.out.println("file name:"+Scheduler.fileName);
        session.connect();
        
        System.out.println("Connection Estabilished");
        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        System.out.println("SFTP connection created");
        
        InputStream out= null;
        
        out= sftpChannel.get(Scheduler.path + Scheduler.fileName);
        
        br = new BufferedReader(new InputStreamReader(out));
        daySerial=0;
        
        saveUtil(br, true);
        
        sftpChannel.disconnect();
        br.close();
        session.disconnect();
		System.out.println("sftp closing");
        
		}
		catch(JSchException e ){
			
	        session.disconnect();
			System.out.println("sftp closing in catch");
		}
		
		
		
		
	}
	
	/**
	 * 
	 * continously reads logs from NMS server
	 * uses 'tail -f' command
	 * 
	 */
	
	public void tail(){
		
		Session session = null;
		
		BufferedReader br1 = null;
		try{
		
	
		JSch jsch = new JSch();
    	
		session = jsch.getSession("iiitd", "192.168.1.74", 22);
	
		session.setPassword("iiitd@456");
		
		
		session.setConfig("StrictHostKeyChecking", "no");
		System.out.println("Establishing Connection...");
        
        session.connect();

        channelExec = (ChannelExec) session.openChannel("exec");
        InputStream stream = channelExec.getInputStream();
        
        channelExec.setCommand("tail -f "+Scheduler.path + Scheduler.fileName);
        
        //channelExec.setCommand("tail -f /home/ironman/Desktop/snmp2015-11-04.log");
        channelExec.connect();
        br1 = new BufferedReader(new InputStreamReader(stream));
        daySerial = 0;
        
        saveUtil(br1, false);
        
        channelExec.disconnect();
        br1.close();
        session.disconnect();
        System.out.println("tail closing");
        
		}
		catch(Exception e ){
			
	        session.disconnect();
			System.out.println("tail closing in catch");
		}
		
	
	}
	
	/**
	 * 
	 * @param br
	 * @param ConnSftp
	 * 
	 * util function for saving the logs
	 */
	
	public void saveUtil(BufferedReader br, boolean ConnSftp){
		
        try {
        	ParseTrap2 parse = new ParseTrap2();
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String line = null;
	        StringBuffer currentTrap = new StringBuffer();
	        Log doc = new Log();
			
	        
	        
        	while (true){
        		//System.out.println(line);
	        		if((line = br.readLine()) != null){
		        		if(line.contains("--")){
		        			//System.out.println("----JMD-----");
							doc = new ParseTrap2().parse(currentTrap.toString());
							if(doc !=null)
							{
								updateDaySerial(doc.getTs());
								doc.setDaySerialNo(daySerial);
								
								/*if(doc.giveMACAddress()!=null && doc.giveMACAddress().equals("010990f5926f6133a7adc635ce971e318d3edeb1928227e6e537882f43ad1b12"))
									System.out.println(doc);
								*/
								
								digest.update((doc.toJson().getBytes(StandardCharsets.UTF_8)));
								byte[] hash = digest.digest();
								
								//System.out.println(String.format("%064x", new java.math.BigInteger(1, hash)));
								//db.getCollection("temploglocal").updateOne(doc, doc, new UpdateOptions().upsert(true));
								doc.setId(String.format("%064x", new java.math.BigInteger(1, hash)) );
								
								//System.out.println(doc.toString());
								
								updateEndTs(doc, daySerial);
								
								logRepository.save(doc);
							}
							
							doc = new Log();
							currentTrap.setLength(0);
							continue;
							
						}
		        		else {
							if(currentTrap.length()!=0){
								//System.out.println("--"+currentTrap);
								
								currentTrap.append("\n"+line);
							}
							else
								currentTrap.append(line);
						}
		        	}
		        	else if(ConnSftp){
	        			//sftp finish
		        		return;
	        		}
		        	else{
		        		//tail -f : wait for input
		        		System.out.println("Line is null");
		        		System.out.println("Scheduler date: " + Scheduler.date);
		        	}
        	}    	
    		
    		
    		
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * starter function for reading logs 
	 * 
	 * it decides if it needs to do sftp or tail
	 */
	
	
	public void parseLogs() throws Exception {
		
		if(!Scheduler.tail)
		{	
			
			if(Scheduler.date.equals(LocalDate.now())){
				Scheduler.fileName = "snmptraps.log";
				Scheduler.tail = true;
				sftp();
			}else{
				Scheduler.fileName = "snmp"+Scheduler.date + ".log";
				sftp();
				Scheduler.date = Scheduler.date.plusDays(1);
			}
		}
		else{
			
			
			tail();
		}
	}
	
	/**
	 * 
	 * @param doc
	 * @param daySerial
	 * 
	 * whenever a new log comes, this function checks the last log from the same
	 * mobile device and updates that log's end time
	 */
	
	private void updateEndTs(Log doc, long daySerial) {
		// TODO Auto-generated method stub
		
		Log temp =null;
		
		if(doc.getOid().toString().equals("ciscoLwappDot11ClientMovedToRunState")
				){
			
			temp = logRepository.findAndUpdateLog(doc.getCldcClientMacAddress(), 
					doc.getTs(), daySerial);
			
			}
		else if(doc.getOid().equals("bsnDot11StationDisassociate")){
				
			temp = logRepository.findAndUpdateLog(doc.getBsnStationMacAddress(), 
					doc.getTs(), daySerial);

			
			}
		else if(doc.getOid().equals("bsnDot11StationDeauthenticate")){
			
			temp = logRepository.findAndUpdateLog(doc.getBsnStationMacAddress(), 
					doc.getTs(), daySerial);

			
			}
		
		
		
	}



	

}
