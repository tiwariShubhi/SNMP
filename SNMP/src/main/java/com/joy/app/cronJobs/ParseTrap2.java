package com.joy.app.cronJobs;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.model.Log;
import com.joy.app.repository.LogRepository;

import static java.util.Arrays.asList;


public class ParseTrap2 {
	
	
	
	String line;
	String date;
	String oid;
	DateFormat format = new SimpleDateFormat(" H : m : s  d - M - yyyy", Locale.ENGLISH);
	Boolean writeFlag;
	Log doc;
	//Document doc;
	BufferedReader scanner;
	
	//MessageDigest digest;
	
	
	ParseTrap2(){
		this.line="";
		this.date="";
		this.oid="";
		this.writeFlag = false;
		this.doc = new Log();
		/*try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	/*public MongoDatabase connectMongoDB(){
		MongoClient mongo = new MongoClient();
		MongoDatabase db = mongo.getDatabase("test");
		System.out.println("Connected to MongoDB");
		return db;
	
	}*/
	
	/**
	 * 
	 * @param mac
	 * @return
	 * 
	 * sometimes the log's MAC field does not have enough zeroes 
	 * this function makes read MAC address consistent with the DB
	 * 
	 */
	
	public String correctMac(String mac){
		
		
		if(mac.matches("^([0-9a-f]{2}[:]){5}([0-9a-f]{2})$")){
			return mac;
		}
		
		else
		{
		
		StringBuffer sb = new StringBuffer(mac);
		StringBuffer correctMac = new StringBuffer();
		StringTokenizer st = new StringTokenizer(mac,":");
		StringBuffer t;
		
		while(st.hasMoreTokens()){
			t = new StringBuffer(st.nextToken());
			if(t.length()==1){
				t.insert(0, '0');
			}
			correctMac.append(":"+t);
		}
		
		return correctMac.substring(1).toString();
		}
		
	}
	
	/**
	 * 
	 * @param mac
	 * @return
	 * 
	 * used for hashing
	 */
	
	public String hash(String mac){
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update((mac.getBytes(StandardCharsets.UTF_8)));
			byte[] hash = digest.digest();
			
			return String.format("%064x", new java.math.BigInteger(1, hash));
			
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
	}
	
	/**
	 * 
	 * @param trap
	 * @return
	 * 
	 * parses each log
	 * 
	 */
	
	public Log parse(String trap){
		/*
		System.out.println("---TRAP---");
		System.out.println(trap);
		System.out.println("---EOT---");
		
		*/
		scanner = new BufferedReader( new StringReader(trap));
		
		//System.out.println("db"+db.toString());
		
		try {
			while ((line = scanner.readLine()) != null ) {
			  //System.out.println("=>"+scanner.nextLine());
			  // process the line
				
				//System.out.println();
				//System.out.println("=> "+ line);
				
			
				if( line.contains("Date:")){
					date = line.substring(line.indexOf(':')+1);
					//System.out.println("date=>"+date);
				}
					
				if( line.contains("SNMPv2-MIB::snmpTrapOID.0")){
					oid = line;
					oid = line.substring(line.lastIndexOf(':')+1);
					//System.out.println("oid: "+oid);
				}
				
		/*	}
		}
			catch(Exception e){
				
			}
		*/
	
				if(!date.isEmpty() && !oid.isEmpty()){
					
					//System.out.println("jai mata di!");
					try {
						doc.setTs(new Date(format.parse(date).getTime() ));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					doc.setOid(oid);
					
					if(oid.contains("bsnDot11StationAssociate") || oid.contains("bsnDot11StationDeauthenticate") || oid.contains("bsnDot11StationDisassociate")){
						if(line.contains("bsnStationAPMacAddr.0")){
							doc.setBsnStationAPMacAddr(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("bsnUserIpAddress.0")){
							doc.setBsnUserIpAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+13))));
						}
						if(line.contains("bsnStationMacAddress.0")){
							doc.setBsnStationMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("bsnAPName.0")){
							doc.setBsnAPName(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
					
					if(oid.contains("ciscoLwappDot11ClientCoverageHolePreAlarm")){
						if(line.contains("cldcClientMacAddress.0")){
							doc.setCldcClientMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cldcApMacAddress.0")){
							doc.setCldcApMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cLApName.0")){
							doc.setcLApName(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("clrRrmNeighborApCount.0")){
							doc.setClrRrmNeighborApCount(line.substring(line.lastIndexOf('=')+57));
						}
						if(line.contains("clrRrmNeighborApMacAddress.0")){
							doc.setClrRrmNeighborApMacAddress(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("clrRrmNeighborApRssi.0")){
							doc.setClrRrmNeighborApRssi(line.substring(line.lastIndexOf('=')+10));
						}
						
						if(line.contains("clrRrmRssiHistogramValues.0")){
							doc.setClrRrmRssiHistogramValues(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
					
					if(oid.contains("ciscoLwappDot11ClientDisassocDataStatsTrap")){
						if(line.contains("cLApName.")){
							doc.setcLApName(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cldcApMacAddress.")){
							doc.setCldcApMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cldcClientSSID.")){
							doc.setCldcClientSSID(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cldcClientSessionID.")){
							doc.setCldcClientSessionID(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
					
					if(oid.contains("ciscoLwappDot11ClientAssocDataStatsTrap")){
						
						if(line.contains("cldcApMacAddress.")){
							doc.setCldcApMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cldcClientSSID.")){
							doc.setCldcClientSSID(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
							
					}
					
					if(oid.contains("ciscoLwappDot11ClientSessionTrap")){
						if(line.contains("cLApName.")){
							doc.setcLApName(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cldcApMacAddress.")){
							doc.setCldcApMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cldcClientSSID.")){
							doc.setCldcClientSSID(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cldcClientSessionID.")){
							doc.setCldcClientSessionID(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
					
					if(oid.contains("ciscoLwappSiIdrDevice")){
						if(line.contains("cLApSysMacAddress.0")){
							doc.setcLApSysMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cLSiIdrDeviceId.0")){
							doc.setcLSiIdrDeviceId(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cLSiIdrDeviceType.0")){
							doc.setcLSiIdrDeviceType(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cLSiIdrAffectedChannels.0")){
							doc.setcLSiIdrAffectedChannels(line.substring(line.lastIndexOf('=')+10));
							
						}
						if(line.contains("cLSiIdrClusterId.0")){
							doc.setcLSiIdrClusterId(line.substring(line.lastIndexOf('=')+10));
							
						}
						if(line.contains("cLSiAlarmClear.0")){
							doc.setcLSiAlarmClear(line.substring(line.lastIndexOf('=')+11));
							
						}
						if(line.contains("cLApName.0")){
							doc.setcLApName(line.substring(line.lastIndexOf('=')+10));
							
						}
						if(line.contains("cLSiIdrPreviousClusterId.0")){
							doc.setcLSiIdrPreviousClusterId(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
					
					
					
					if(oid.contains("ciscoLwappDot11ClientMovedToRunState")){
						if(line.contains("cldcClientMacAddress.0")){
							doc.setCldcClientMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cLApName")){
							doc.setcLApName(line.substring(line.lastIndexOf('=')+10));
						}
						if(line.contains("cldcApMacAddress.0")){
							doc.setCldcApMacAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+10))));
						}
						if(line.contains("cldcClientIPAddress.0")){
							doc.setCldcClientIPAddress(hash(correctMac(line.substring(line.lastIndexOf('=')+13))));
						}
						if(line.contains("cldcClientSSID.0")){
							doc.setCldcClientSSID(line.substring(line.lastIndexOf('=')+10));
							writeFlag=true;
						}
						
					}
						try {
							if(writeFlag){
								
								
								//UUID uid = UUID.fromString("joy");
								//System.out.println("Check "+uid.randomUUID());
								
								//db.getCollection("temploglocal").insertOne(doc);
								//System.out.println("jai mataa di!");
								//System.out.println(date);
								Background.progressDate = date;
								return doc;
								
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							e.printStackTrace();
							break;
						}
				}
			}
			
			//System.out.println("done!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		//scanner.close();
		//System.err.println("Parsing Unsuccesful!");
		return null;
		
		
		
			
	}
	
}
