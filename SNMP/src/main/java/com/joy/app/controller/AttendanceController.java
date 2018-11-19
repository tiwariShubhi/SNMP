package com.joy.app.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joy.app.model.Attendance;
import com.joy.app.model.AttendanceLog;
import com.joy.app.model.Log;
import com.joy.app.model.NamedHashEncryptMac;
import com.joy.app.repository.LogRepository;
import com.joy.app.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author joy
 *
 */

@Controller
public class AttendanceController {
	
	
	@Autowired
	private LogRepository logRepository;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	@RequestMapping(value = "/attendance_marker", method = RequestMethod.GET)
//	public String showMarker(HttpSession session) {
//		if(session.getAttribute("id_token")==null 
//				)
//			
//			return "login";
//		
//		return "attendance_marker";
//	}

	@RequestMapping(value = "/apinfo", method = RequestMethod.GET)
	public String showMarker(HttpSession session) {
//		if(session.getAttribute("id_token")==null
//				)
//
//			return "login";

		return "access_point_info";
	}


	@RequestMapping(value = "/apconnections", method = RequestMethod.POST)
	public String accessPointConnections(@RequestParam("location") List<String> hapmacs,
								   @RequestParam("duration") String duration, //duration is in minutes
								   @RequestParam("fromDateMillis") String datefrom,
										 @RequestParam("toDateMillis") String dateto,
										 HttpSession session)
	{
		//if(session.getAttribute("id_token")==null)
			//return null;

		//LocalDateTime a1 = Instant.ofEpochMilli(Long.parseLong("1538103600000")).atZone(ZoneId.systemDefault()).toLocalDateTime();
		//LocalDateTime a2 = Instant.ofEpochMilli(Long.parseLong("1538114400000")).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime a1 = Instant.ofEpochMilli(Long.parseLong(datefrom)).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime a2 = Instant.ofEpochMilli(Long.parseLong(dateto)).atZone(ZoneId.systemDefault()).toLocalDateTime();
		Date a1Converted = Date.from(a1.atZone(ZoneId.systemDefault()).toInstant());
		Date a2Converted = Date.from(a2.atZone(ZoneId.systemDefault()).toInstant());

		//List<Log> oA = logRepository.findHashedApMacRunState("f84094f40f793db34c13a24e5b68111e535cf89a0b84202c1a1e32296be6bc73",
		//        Date.from(a1.atZone(ZoneId.systemDefault()).toInstant()),
		//        Date.from(a2.atZone(ZoneId.systemDefault()).toInstant()));

		Map<String, Long> hmacDurationTracker = new HashMap<>();
		for (String hapmac: hapmacs) {
			List<Log> presentLogs = logRepository.findHashedApMacRunState(hapmac, a1Converted, a2Converted);
			for (Log currentLog : presentLogs) {
				if (currentLog.getEndTs() != null) {
					long endTsMillis = currentLog.getEndTs().getTime();
					long startTsMillis = currentLog.getTs().getTime();
					if (startTsMillis < a1Converted.getTime()) {
						startTsMillis = a1Converted.getTime();
					}
					if (endTsMillis > a2Converted.getTime()) {
						endTsMillis = a2Converted.getTime();
					}
					long timeDuration = endTsMillis - startTsMillis;

					if (hmacDurationTracker.containsKey(currentLog.getCldcClientMacAddress())) {
						long existingTime = hmacDurationTracker.get(currentLog.getCldcClientMacAddress());
						hmacDurationTracker.put(currentLog.getCldcClientMacAddress(), timeDuration + existingTime);
					}
					else {
						//long timeDuration = currentLog.getEndTs().getTime() - currentLog.getTs().getTime();
						hmacDurationTracker.put(currentLog.getCldcClientMacAddress(), timeDuration);
					}
				}
				else {
					//glitch in system/records
					//System.out.println(currentLog);
				}
			}
		}


		List<String> positiveHmacs = new ArrayList<>();

		System.out.println(hmacDurationTracker);

		for (Map.Entry<String, Long> entry: hmacDurationTracker.entrySet()) {
			long minutes = entry.getValue() / (60 * 1000);
			long thresholdMinutes = Long.parseLong(duration);
			if (minutes >= thresholdMinutes) {
				positiveHmacs.add(entry.getKey());
			}
		}

		for (String hmac : positiveHmacs) {
			System.out.println(hmac);
		}

		System.out.println("Size: " + positiveHmacs.size());

		return null;
		//return "attendance_marker";
	}
	
	@RequestMapping(value = "/presencecalendar", method = RequestMethod.GET)
	public String showCalendar(
			Model model,
			@RequestParam(value = "hashedmac") String hmac,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		
		
		model.addAttribute("attendance",
				getAttendancenew(hmac,"1448908200000", String.valueOf(System.currentTimeMillis()))		);
		return "calendar";
		
	}
	
	@RequestMapping(value = "/calendar", method = RequestMethod.GET)
	public String showLimitedCalendar(
			Model model,
			@RequestParam(value = "hashedmac") String hmac,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			
			return "login";
		

		model.addAttribute("attendance",
				getAttendancenew(hmac,"1448908200000", String.valueOf(System.currentTimeMillis()))		);
		

		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		
		
		return "limited_calendar";
		
	}
	
	@RequestMapping(value = "data/attendance", method = RequestMethod.GET)
	public @ResponseBody AttendanceLog getCalendarData(
			
			@RequestParam(value = "hashedmac") String hmac,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			
			return null;
		

		return getAttendancenew(hmac,"1448908200000", String.valueOf(System.currentTimeMillis()));
		
	}
	
	
	
	@RequestMapping(value = "/admin/custom-attendance", method = RequestMethod.GET)
	public String showCustomCalendar(
			Model model,
			@RequestParam(value = "hashedmac") String hmac,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
				
		return "custom_calendar";
		
	}
	
	@RequestMapping(value = "admin/data/custom-attendance", method = RequestMethod.GET)
	public @ResponseBody AttendanceLog getCustomCalendarData(
			
			@RequestParam(value = "hashedmac") String hmac,
			@RequestParam(value = "start") int startHour,
			@RequestParam(value = "end") int endHour,
			@RequestParam(value = "locationid") int location_ind,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			{System.out.println("Not Authorized");
			return null;}
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		return getCustomAttendance(hmac, "1448908200000", String.valueOf(System.currentTimeMillis()),
				startHour, endHour, location_ind) ;
		
	}
	

	
	private AttendanceLog getAttendancenew(String hmac, String from, String to){
		
		
		AttendanceLog alog = new AttendanceLog();
		alog.setHmac(hmac);
		alog.setValidFrom(new Date(Long.parseLong(from)));
		alog.setValidUpto(new Date(Long.parseLong(to)));
		
		logger.info("updating attendance data new ");
		System.out.println(hmac +" from "+alog.getValidFrom()+" to "+alog.getValidUpto());
		
		
		LocalDateTime from_ldt = Instant.ofEpochMilli(Long.parseLong(from)).atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN);
		LocalDateTime to_ldt = Instant.ofEpochMilli(Long.parseLong(to)).atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1).with(LocalTime.MIN);
		
		List<Log> logs = logRepository.findByHashedMacDuration(hmac,
				Date.from(from_ldt.atZone(ZoneId.systemDefault()).toInstant()),
				Date.from(to_ldt.atZone(ZoneId.systemDefault()).toInstant())
				);
		
		LocalDateTime start = to_ldt.minusDays(1).withHour(10);
		LocalDateTime end = to_ldt.minusDays(1).withHour(22);
		LocalDate pre = null;
		
		for(Log log: logs){
			LocalDateTime ldt = LocalDateTime.ofInstant(log.getTs().toInstant(), ZoneId.systemDefault());
			//System.out.println(ldt+ "start "+start+" end "+end);
			if(ldt.compareTo(start)< 0){
				//System.out.println("less "+ldt);
				start = ldt.with(LocalTime.MIN).withHour(10);
				end = ldt.with(LocalTime.MIN).withHour(22);
			}
			if(ldt.compareTo(start)>=0 && ldt.compareTo(end)<=0){
				//System.out.println("in between "+ldt +" loc "+log.getcLApName());
				if(!ldt.toLocalDate().equals(pre) && log.getcLApName()!=null && log.getcLApName().matches("^(ACB|LB|LCB)(.*)")){
					 System.out.println(log);
					 pre =  start.toLocalDate();
					 Attendance t = new Attendance();
					 t.setTitle("✔");
					 t.setStartDate(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
					 t.setStart(start.toLocalDate().toString());
					 
					 alog.getAttendList().add(t);
					 System.out.println("Present on "+ t.getStart());
					 
				 }
			}
			if(ldt.compareTo(end)>0){
				//System.out.println("more "+ldt);
			}
		}
		
		
		System.out.println(alog);
		
		
		
		
		return alog;
	}
	
	private AttendanceLog getCustomAttendance(
			String hmac, String from, String to, int startHour, int endHour, int location_ind){
				
		String regex = LocationRegexGen(location_ind);
		
		AttendanceLog alog = new AttendanceLog();
		alog.setHmac(hmac);
		alog.setValidFrom(new Date(Long.parseLong(from)));
		alog.setValidUpto(new Date(Long.parseLong(to)));
		
		logger.info("updating attendance data new ");
		System.out.println(hmac +" from "+alog.getValidFrom()+" to "+alog.getValidUpto());
		
		
		LocalDateTime from_ldt = Instant.ofEpochMilli(Long.parseLong(from)).atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN);
		LocalDateTime to_ldt = Instant.ofEpochMilli(Long.parseLong(to)).atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1).with(LocalTime.MIN);
		
		List<Log> logs = logRepository.findByHashedMacDuration(hmac,
				Date.from(from_ldt.atZone(ZoneId.systemDefault()).toInstant()),
				Date.from(to_ldt.atZone(ZoneId.systemDefault()).toInstant())
				);
		
		LocalDateTime start = to_ldt.minusDays(1).withHour(startHour);
		LocalDateTime end = to_ldt.minusDays(1).withHour(endHour);
		LocalDate pre = null;
		Attendance t =null;
		
		for(Log log: logs){
			LocalDateTime ldt = LocalDateTime.ofInstant(log.getTs().toInstant(), ZoneId.systemDefault());
			//System.out.println(ldt+ "start "+start+" end "+end);
			if(ldt.compareTo(start)< 0){
				//System.out.println("less "+ldt);
				start = ldt.with(LocalTime.MIN).withHour(startHour);
				end = ldt.with(LocalTime.MIN).withHour(endHour);
			}
			if(ldt.compareTo(start)>=0 && ldt.compareTo(end)<=0){
				//System.out.println("in between "+ldt +" loc "+log.getcLApName());
				if(log.getcLApName()!=null && log.getcLApName().matches(regex)){
					 System.out.println(log);
					 if(!ldt.toLocalDate().equals(pre)){
						 pre =  start.toLocalDate();
						 t = new Attendance();
						 t.setTitle("✔");
						 t.setStartDate(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
						 t.setStart(start.toLocalDate().toString());
						 Period p = new Period(new DateTime(log.getTs()),new DateTime(log.getEndTs()!=null?log.getEndTs():log.getTs()));
							
						 t.setAcadAreaMillis(p.toStandardDuration().getMillis());
						 alog.getAttendList().add(t);
					 }
					 else{
						 Period p = new Period(new DateTime(log.getTs()),new DateTime(log.getEndTs()!=null?log.getEndTs():log.getTs()));
	
						 t.setAcadAreaMillis(t.getAcadAreaMillis()+p.toStandardDuration().getMillis());
						 System.out.println("minutes on "+ t.getStart()+" "+t.getAcadAreaMillis());
					 }
					 System.out.println("Present on "+ t.getStart());
					 
				 }
			}
			if(ldt.compareTo(end)>0){
				//System.out.println("more "+ldt);
			}
		}
		
		
		System.out.println(alog);
		
		
		
		
		return alog;
		
		
	}
	
	
	private String LocationRegexGen(int location_ind){
		
		switch(location_ind){
			
		case 0: return "^(ACB|LB|LCB)(.*)";
		case 1: return "^(ACB|LB|LCB|DB2)(.*)";
		case 2: return "^ACB(.*)";
		case 3: return "^LB(.*)";
		case 4: return "^LCB(.*)";
		case 5: return "^DB2(.*)";
		case 6: return "(.*)";
		default: return "^(ACB|LB|LCB)(.*)";
		}
	}
	
	/*@RequestMapping(value = "admin/update/attendance", method = RequestMethod.GET)
	public @ResponseBody AttendanceLog updateCalendarData(
			
			@RequestParam(value = "hashedmac") String hmac,
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			{System.out.println("Not Authorized");
			return null;}
		
		
		
		AttendanceLog alog = new AttendanceLog();
		alog.setHmac(hmac);
		alog.setValidFrom(new Date(Long.parseLong(from)));
		alog.setValidUpto(new Date(Long.parseLong(to)));
		
		logger.info("updating attendance data ");
		System.out.println(hmac +" from "+alog.getValidFrom()+" to "+alog.getValidUpto());
		
		
		LocalDateTime from_ldt = Instant.ofEpochMilli(Long.parseLong(from)).atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN);
		LocalDateTime to_ldt = Instant.ofEpochMilli(Long.parseLong(to)).atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1).with(LocalTime.MIN);
		
		
		LocalDateTime start = from_ldt.withHour(10);
		LocalDateTime end = from_ldt.withHour(22);
		
		for(; end.isBefore(to_ldt); start = start.plusDays(1), end = end.plusDays(1)){
			
			List<Log> logs = logRepository.findByHashedMacDuration(hmac,
					Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
					Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
					);
			
			for (Log log : logs){
				
				 if(log.getcLApName()!=null && log.getcLApName().matches("^(ACB|LB|LCB)(.*)")){
					 System.out.println(log);
					 Attendance t = new Attendance();
					 t.setTitle("Present");
					 t.setStartDate(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
					 t.setStart(start.toLocalDate().toString());
					 
					 alog.getAttendList().add(t);
					 System.out.println("Present on "+ t.getStart());
					 break;
				 }
			}
		}
		
			
		System.out.println(alog);
		attendanceLogRepository.save(alog);
		
		
		
		return alog;
		
		
	}*/
	
	/*@RequestMapping(value = "admin/update/attendance/new", method = RequestMethod.GET)
	public @ResponseBody AttendanceLog updateCalendarDataNew(
			
			@RequestParam(value = "hashedmac") String hmac,
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			{System.out.println("Not Authorized");
			return null;}
		
		
		return getAttendancenew(hmac, from, to);
		
		
	}*/
	
}
