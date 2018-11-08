package com.joy.app.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joy.app.model.FilteredLog;
import com.joy.app.model.Log;
import com.joy.app.repository.LogRepository;

@Controller
public class MultiViewLogController {

	@Autowired
	LogRepository logRepository;
	
	PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
	.appendYears()
    .appendSuffix(" year")
    .appendSeparator(", ")
	.appendMonths()
    .appendSuffix(" month")
    .appendSeparator(", ")
	.appendWeeks()
    .appendSuffix(" week")
    .appendSeparator(", ")
	.appendDays()
    .appendSuffix(" day")
    .appendSeparator(", ")
    .appendHours()
    .appendSuffix( " hr")
    .appendSeparator(" ")
    .appendMinutes()
    .appendSuffix(" min")
    .appendSeparator(" ")
    .appendSeconds()
    .appendSuffix(" sec")
    
    .toFormatter();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@RequestMapping(value = "/admin/trackuser/multiview", method = RequestMethod.GET)
	public String trackUser(Model track,
			@RequestParam(value = "hmac1") String hmac1,
			@RequestParam(value = "hmac2") String hmac2, HttpSession session) {
		
		
		if(session.getAttribute("id_token")==null)
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			track.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		logger.info("GET Multiple Hash Trackuser");
	    System.out.println("hmac1= "+hmac1 +"hmac2= "+hmac2 );
	    
		
		List<Log> logs = logRepository.findByMultipleHashedMac(hmac1, hmac2);
		
		List<FilteredLog> filLogs = new ArrayList<FilteredLog>();
		FilteredLog flog = new FilteredLog();
		int one=-1, two=-1, index=-1;
		
		
		for(int i=0;i<logs.size(); i++ ){
			
			Log temp = logs.get(i);
			
			if( temp.giveMACAddress().equals(hmac1)){
				if(one==-1){
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(new Date());
					filLogs.add(flog);
					index++;
					//System.out.println(flog.toString());
					one = index;
				}
				else if(filLogs.get(one).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
						temp.giveAPName().replaceFirst("\\d+$", ""))){
					//System.out.println("one = "+filLogs.get(one).getLog().giveAPName().replaceFirst("\\d+$", ""));
					//System.out.println("temp = "+temp.giveAPName().replaceFirst("\\d+$", ""));
					
					filLogs.get(one).setStartTime(temp.getTs());
					//System.out.println(filLogs.get(one));
				}
				else{
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(temp.getTs());
					filLogs.add(flog);
					index++;
					//System.out.println(flog.toString());
					one = index;
				}
			}
			else {
				if(two==-1){
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(new Date());
					filLogs.add(flog);
					index++;
					//System.out.println("two1"+flog.toString());
					two = index;
				}
				else if(filLogs.get(two).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
						temp.giveAPName().replaceFirst("\\d+$", ""))){
					//System.out.println("two = "+filLogs.get(two).getLog().giveAPName().replaceFirst("\\d+$", ""));
					//System.out.println("temp = "+temp.giveAPName().replaceFirst("\\d+$", ""));
					
					filLogs.get(two).setStartTime(temp.getTs());
				}
				else{
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(temp.getTs());
					filLogs.add(flog);
					index++;
					//System.out.println("two"+flog.toString());
					two = index;
				}
			}
			
			
			
		}
		
		
		
		//System.out.println("filtered results");
		for (int j = 0; j < filLogs.size() ; j++){
		
			//FilteredLog temp = filLogs.get(i);
			Period p = new Period(new DateTime(filLogs.get(j).getStartTime()),new DateTime(filLogs.get(j).getEndTime()));
		
			filLogs.get(j).setDuration(daysHoursMinutes.print(p.normalizedStandard()));
			//System.out.println(filLogs.get(j));
		}

		//durations.add(daysHoursMinutes.print(new Period(new DateTime(logs.get(logs.size()-1).getTs()),savedDate).normalizedStandard()));
		track.addAttribute("logs", filLogs);
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			track.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			track.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		track.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		logger.info("Request completed GET Multiple Hash Trackuser");
	    System.out.println("hmac1= "+hmac1 +"hmac2= "+hmac2 );
	    
		
		return "schedule_multi";
	}
	
	
	
	
	@RequestMapping(value = "/admin/trackuser/multiview/duration", method = RequestMethod.GET)
	public String trackUserDuration(Model track,
			@RequestParam(value = "hmac1") String hmac1,
			@RequestParam(value = "hmac2") String hmac2,
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to")String to, HttpSession session) {
		
		
		if(session.getAttribute("id_token")==null)
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			track.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		logger.info("GET Multiple Hash Trackuser Duration");
	    System.out.println("hmac1= "+hmac1 +"hmac2= "+hmac2 );
	    
		
		List<Log> logs = logRepository.findByMultipleHashedMacDuration(hmac1, hmac2, new Date(Long.parseLong(from)), new Date(Long.parseLong(to)));
		
		List<FilteredLog> filLogs = new ArrayList<FilteredLog>();
		FilteredLog flog = new FilteredLog();
		int one=-1, two=-1, index=-1;
		
		
		for(int i=0;i<logs.size(); i++ ){
			
			Log temp = logs.get(i);
			
			if( temp.giveMACAddress().equals(hmac1)){
				if(one==-1){
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(new Date());
					filLogs.add(flog);
					index++;
					//System.out.println(flog.toString());
					one = index;
				}
				else if(filLogs.get(one).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
						temp.giveAPName().replaceFirst("\\d+$", ""))){
					//System.out.println("one = "+filLogs.get(one).getLog().giveAPName().replaceFirst("\\d+$", ""));
					//System.out.println("temp = "+temp.giveAPName().replaceFirst("\\d+$", ""));
					
					filLogs.get(one).setStartTime(temp.getTs());
					//System.out.println(filLogs.get(one));
				}
				else{
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(temp.getTs());
					filLogs.add(flog);
					index++;
					//System.out.println(flog.toString());
					one = index;
				}
			}
			else {
				if(two==-1){
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(new Date());
					filLogs.add(flog);
					index++;
					//System.out.println("two1"+flog.toString());
					two = index;
				}
				else if(filLogs.get(two).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
						temp.giveAPName().replaceFirst("\\d+$", ""))){
					//System.out.println("two = "+filLogs.get(two).getLog().giveAPName().replaceFirst("\\d+$", ""));
					//System.out.println("temp = "+temp.giveAPName().replaceFirst("\\d+$", ""));
					
					filLogs.get(two).setStartTime(temp.getTs());
				}
				else{
					flog= new FilteredLog();
					flog.setLog(temp);
					flog.setStartTime(temp.getTs());
					if(temp.getEndTs()!=null)
						flog.setEndTime(temp.getEndTs());
					else
						flog.setEndTime(temp.getTs());
					filLogs.add(flog);
					index++;
					//System.out.println("two"+flog.toString());
					two = index;
				}
			}
			
			
			
		}
		
		
		
		//System.out.println("filtered results");
		for (int j = 0; j < filLogs.size() ; j++){
		
			//FilteredLog temp = filLogs.get(i);
			Period p = new Period(new DateTime(filLogs.get(j).getStartTime()),new DateTime(filLogs.get(j).getEndTime()));
		
			filLogs.get(j).setDuration(daysHoursMinutes.print(p.normalizedStandard()));
			//System.out.println(filLogs.get(j));
		}

		//durations.add(daysHoursMinutes.print(new Period(new DateTime(logs.get(logs.size()-1).getTs()),savedDate).normalizedStandard()));
		track.addAttribute("logs", filLogs);
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			track.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			track.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		track.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		logger.info("Request completed GET Multiple Hash Trackuser duration");
	    System.out.println("hmac1= "+hmac1 +"hmac2= "+hmac2 );
	    
		
		
		return "schedule_multi";
	}
	
	
}
