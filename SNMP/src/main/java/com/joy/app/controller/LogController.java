package com.joy.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.model.FilteredLog;
import com.joy.app.model.FootfallData;
import com.joy.app.model.Log;
import com.joy.app.model.TimeSpent;
import com.joy.app.model.User;
import com.joy.app.repository.LogRepository;
import com.joy.app.repository.UserRepository;

@Controller
public class LogController {

	@Autowired
	private LogRepository logRepository;
	@Autowired
	private UserRepository userRepository;
	
	
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
	
	
//	@RequestMapping(value = "/attendance_marker", method = RequestMethod.GET)
//	public String showMarker(HttpSession session) {
//		if(session.getAttribute("id_token")==null 
//				)
//			
//			return "login";
//		
//		return "attendance_marker";
//	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showMarker(HttpSession session) {
//		if(session.getAttribute("id_token")==null 
//				)
//			
//			return "login";
		
		return "attendance_marker";
	}
	
	@RequestMapping(value = "/admin/trackuser", method = RequestMethod.GET)
	public String trackUser(Model track,
			@RequestParam(value = "hashedmac") String mac,
			@RequestParam(value = "skip", required = false) Integer skip, HttpSession session) {

		if(session.getAttribute("id_token")==null)
			return "login";
		
		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			track.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
	    logger.info("GET Trackuser");
	    System.out.println("hmac= "+mac);
	   
		List<Log> logs = logRepository.findByHashedMac(mac);
		
		
		
		
		List<FilteredLog> filLogs = new ArrayList<FilteredLog>();
		FilteredLog flog = new FilteredLog();
		int index=-1;
		
		if(logs!=null && !logs.isEmpty() &&logs.get(0)!=null){
			flog = new FilteredLog();
			flog.setLog(logs.get(0));
			flog.setStartTime(logs.get(0).getTs());
			if(logs.get(0).getEndTs()!=null)
				flog.setEndTime(logs.get(0).getEndTs());
			else
				flog.setEndTime(new Date());
			filLogs.add(flog);
			index++;
			
		}
		
		for(int i=1;i<logs.size(); i++ ){
			
			Log temp = logs.get(i);
			
			if( filLogs.get(index).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
					temp.giveAPName().replaceFirst("\\d+$", ""))){
				
				filLogs.get(index).setStartTime(temp.getTs());
			}
			else{
				flog = new FilteredLog();
				flog.setLog(temp);
				flog.setStartTime(temp.getTs());
				if(temp.getEndTs()!=null)
					flog.setEndTime(temp.getEndTs());
				else
					flog.setEndTime(temp.getTs());
				filLogs.add(flog);
				index++;
				
			}
				
		}
		
		
		
		for (int j = 0; j < filLogs.size() ; j++){
		
			
			Period p = new Period(new DateTime(filLogs.get(j).getStartTime()),new DateTime(filLogs.get(j).getEndTime()));
		
			filLogs.get(j).setDuration(daysHoursMinutes.print(p.normalizedStandard()));
			
		}

		
		track.addAttribute("logs", filLogs);
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			track.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			track.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		track.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		logger.info("Request completed GET Trackuser");
	    System.out.println("hmac= "+mac);
		
		return "schedule_continous";

	}
	
	@RequestMapping(value = "/admin/trackuser/duration", method = RequestMethod.GET)
	public String trackUserDuration(
			Model track,
			@RequestParam(value = "hashedmac") String mac,
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to")String to, HttpSession session) {

		if(session.getAttribute("id_token")==null)
			return "login";
		
		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			track.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		logger.info("GET Trackuser Duration");
	    System.out.println("hmac= "+mac + "from = "+new Date(Long.parseLong(from)) + " to= "+ new Date(Long.parseLong(to)));
	    
		List<Log> logs = logRepository.findByHashedMacDuration(mac, new Date(Long.parseLong(from)), new Date(Long.parseLong(to)));

		
		List<FilteredLog> filLogs = new ArrayList<FilteredLog>();
		FilteredLog flog = new FilteredLog();
		int index=-1;
		
		if(logs!=null && !logs.isEmpty() && logs.get(0)!=null){
			flog = new FilteredLog();
			flog.setLog(logs.get(0));
			flog.setStartTime(logs.get(0).getTs());
			if(logs.get(0).getEndTs()!=null)
				flog.setEndTime(logs.get(0).getEndTs());
			else
				flog.setEndTime(logs.get(0).getTs());
			filLogs.add(flog);
			index++;
			
		}
		
		for(int i=1;i<logs.size(); i++ ){
			
			Log temp = logs.get(i);
			
			if( filLogs.get(index).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
					temp.giveAPName().replaceFirst("\\d+$", ""))){
				
				filLogs.get(index).setStartTime(temp.getTs());
			}
			else{
				flog = new FilteredLog();
				flog.setLog(temp);
				flog.setStartTime(temp.getTs());
				if(temp.getEndTs()!=null)
					flog.setEndTime(temp.getEndTs());
				else
					flog.setEndTime(temp.getTs());
				filLogs.add(flog);
				index++;
				
			}
				
		}
		
		
		
		for (int j = 0; j < filLogs.size() ; j++){
		
			
			Period p = new Period(new DateTime(filLogs.get(j).getStartTime()),new DateTime(filLogs.get(j).getEndTime()));
		
			filLogs.get(j).setDuration(daysHoursMinutes.print(p.normalizedStandard()));
			
		}

		
		
		track.addAttribute("logs", filLogs);
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			track.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			track.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		track.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		logger.info("Request completed GET Trackuser Duration");
	    System.out.println("hmac= "+mac + "from = "+new Date(Long.parseLong(from)) + " to= "+ new Date(Long.parseLong(to)));
	    
		
		return "schedule_duration";

	}

	
	@RequestMapping(value = "/admin/trackuser/charts", method = RequestMethod.GET)
	public String trackuser_charts(
			Model track,
			@RequestParam(value = "hashedmac") String mac, 
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			track.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			track.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			track.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		track.addAttribute("user",((JSONObject)session.getAttribute("data")));
		
		return "schedule_charts";

	}
	
	@RequestMapping(value = "/admin/profile", method = RequestMethod.GET)
	public String track(
			Model profile,
			@RequestParam(value = "hashedmac") String mac, 
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			profile.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		System.out.println("test");
		System.out.println(session.getAttribute("data"));
		//JSONObject data = new JSONObject(session.getAttribute("data"));
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			profile.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			//data.picture = "http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
			
			System.out.println(((JSONObject)session.getAttribute("data")));
			
			profile.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		String t=null;
		/*if(logRepository.firstInAcademicAreaToday(mac)!=null)
		 t = logRepository.firstInAcademicAreaToday(mac).getTs().toString();
		else
			t="--Not Present--";
		*/
		profile.addAttribute("user",((JSONObject)session.getAttribute("data")));
		//profile.addAttribute("entry",t);
		//profile.addAttribute("avgEntry",logRepository.avgAcademicAreaEntry(mac));
		
		
		System.out.println("yolo");
		return "profile";

	}
	
	@RequestMapping(value = "/admin/crowd", method = RequestMethod.GET)
	public String track(
			Model model,
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			//data.picture = "http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
			
			System.out.println(((JSONObject)session.getAttribute("data")));
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		String t=null;
		/*if(logRepository.firstInAcademicAreaToday(mac)!=null)
		 t = logRepository.firstInAcademicAreaToday(mac).getTs().toString();
		else
			t="--Not Present--";
		*/
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		model.addAttribute("userobj",(User)session.getAttribute("userobj"));
		//profile.addAttribute("entry",t);
		//profile.addAttribute("avgEntry",logRepository.avgAcademicAreaEntry(mac));
		//System.out.println("yolo");
		
		return "crowd_analysis";

	}
	
	@RequestMapping(value = "/admin/timespent", method = RequestMethod.GET)
	public String timespent(
			Model model,
			@RequestParam(value = "hashedmac") String mac,
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			//data.picture = "http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
			
			System.out.println(((JSONObject)session.getAttribute("data")));
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		String t=null;
		/*if(logRepository.firstInAcademicAreaToday(mac)!=null)
		 t = logRepository.firstInAcademicAreaToday(mac).getTs().toString();
		else
			t="--Not Present--";
		*/
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		//profile.addAttribute("entry",t);
		//profile.addAttribute("avgEntry",logRepository.avgAcademicAreaEntry(mac));
		//System.out.println("yolo");
		
		return "time_spent";

	}
	

//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	public String login(HttpSession session) {
//
//		// session id_token exists
//
//		return "login";
//
//	}
//
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcomeGet(HttpSession session) {

		// session id_token exists

		return "login";

	}
	
	
	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	public String welcome( Model model,
			@RequestParam("id_token") String id_token, HttpSession session) {
		
		try {
			String response = sendGet("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="
					+ id_token);
			
			System.out.println(response);
			JSONObject json = new JSONObject(response);
			
			if(json.has("hd") && json.getString("hd").equals("iiitd.ac.in") ){
				
				User user = userRepository.findByEmail(json.getString("email"));
				
				if(user!=null && user.isAdmin()==true)
					session.setAttribute("admin", true);
				else
					session.setAttribute("admin", false);
				
				if(user!=null && user.isSuper_user()==true)
					session.setAttribute("super", true);
				else
					session.setAttribute("super", false);
				
				session.setAttribute("id_token",id_token );
				session.setAttribute("email", json.getString("email"));
				session.setAttribute("data", json);
				session.setAttribute("userobj", user);
				model.addAttribute("user", json);
				model.addAttribute("userobj",user);
				
				
				if(((JSONObject) session.getAttribute("data")).has("picture"))
					model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
				else{
					
					
					model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
				}
				
				if((boolean)session.getAttribute("admin") || (boolean)session.getAttribute("super"))
					return "welcome";
				else{
					System.out.println("limited_welcome");
					return "limited_welcome";
				}
			}
			else{
				
				
				model.addAttribute("code","Please use your IIIT Delhi domain, Gaijin!");
				return "errorpage";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("code","Session Expired");
		return "errorpage";

	}
	
	@RequestMapping(value = "/admin/addadmin", method = RequestMethod.GET)
	public String addAdmin(Model model, HttpSession session) {

		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin")){
			
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		return "add_admin";

	}
	
	@RequestMapping(value = "/admin/addsuper", method = RequestMethod.GET)
	public String addSuper(Model model, HttpSession session) {

		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin")){
			
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		return "add_super";

	}
	
	

	@RequestMapping(value = "/footfall/data", method = RequestMethod.GET)
	public @ResponseBody String footfall(
			
			@RequestParam("from") Long from,@RequestParam("to") Long to,
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return null;

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		FootfallData data = logRepository.footfallData(new Date(from), new Date(to));
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "--Not Present--";
		}

	}
	
	
	@RequestMapping(value = "/admin/footfall", method = RequestMethod.GET)
	public String footfall( Model model,
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		if(((JSONObject) session.getAttribute("data")).has("picture"))
			model.addAttribute("img",((JSONObject)session.getAttribute("data")).getString("picture"));
		else{
			//data.picture = "http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
			
			System.out.println(((JSONObject)session.getAttribute("data")));
			
			model.addAttribute("img","http://orphanwisdom.com/wp-content/uploads/2012/07/Facebook-Blank-Photo1.jpg");
		}
		String t=null;
		/*if(logRepository.firstInAcademicAreaToday(mac)!=null)
		 t = logRepository.firstInAcademicAreaToday(mac).getTs().toString();
		else
			t="--Not Present--";
		*/
		model.addAttribute("user",((JSONObject)session.getAttribute("data")));
		model.addAttribute("userobj",(User)session.getAttribute("userobj"));
		
		return "footfall";

	}
	
	
	

	@RequestMapping(value = "/errorpage/{str}", method = RequestMethod.GET)
	public String error(@PathVariable("str") String str, Model model,HttpSession session) {
		
		// session id_token exists
		
		model.addAttribute("code",str);
		
		session.invalidate();
		return "errorpage";

	}
	
	
	
	/* Metrics */
	
	@RequestMapping(value = "/metric/entryacademic", method = RequestMethod.GET)
	public @ResponseBody String entryAcademic(@RequestParam("hashedmac") String hash, HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		if(logRepository.firstInAcademicAreaToday(hash) != null){
			Date d = logRepository.firstInAcademicAreaToday(hash).getTs();
			return d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
		else{
			return "--Not Present--";
		}
		

	}

	
	@RequestMapping(value = "/metric/avgentryacademic", method = RequestMethod.GET)
	public @ResponseBody String entryAcademicAvg(@RequestParam("hashedmac") String hash, HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;


		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		if(logRepository.avgAcademicAreaEntry(hash) != null){
			Date d = logRepository.avgAcademicAreaEntry(hash);
			return d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
		else
			return "--Not Present--";
		
		

	}
	
	
	@RequestMapping(value = "/metric/exitacademic", method = RequestMethod.GET)
	public @ResponseBody String exitAcademic(@RequestParam("hashedmac") String hash, HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;
		


		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		if(logRepository.exitAcademicArea(hash) != null){
			Date d = logRepository.exitAcademicArea(hash).getTs();
			return d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
		else{
			return "--Not Present--";
		}
		

	}

	
	@RequestMapping(value = "/metric/avgexitacademic", method = RequestMethod.GET)
	public @ResponseBody String exitAcademicAvg(@RequestParam("hashedmac") String hash, HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}

		if(logRepository.avgAcademicAreaExit(hash) != null){
			Date d = logRepository.avgAcademicAreaExit(hash);
			return d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
		else
			return "--Not Present--";
		
		

	}
	
	
	/*@RequestMapping(value = "/metric/avgacademictime", method = RequestMethod.GET)
	public @ResponseBody String avgHoursAcademic(
			@RequestParam("hashedmac") String hash, 
			
			HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;
		
		if(logRepository.avgAcademicAreaExit(hash) != null){
			Date d = logRepository.avgAcademicHours(hash);
			return d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
		else
			return "--Not Present--";
		
		
	}
*/

	@RequestMapping(value = "/metric/timespent", method = RequestMethod.GET)
	public @ResponseBody String hoursAcademic(
			@RequestParam("hashedmac") String hash, 
			@RequestParam("from") String from, 
			@RequestParam("to") String to, 
			HttpSession session) {
		
		// session id_token exists
		if(session.getAttribute("id_token")==null)
			return null;
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}

		
		return getTimeSpentData(hash, from, to);
	}
	
	
	
	
	private String getTimeSpentData(String hash, String from, String to){
List<Log> logs = logRepository.findByHashedMacDuration(hash, new Date(Long.parseLong(from)), new Date(Long.parseLong(to)));

		
		List<FilteredLog> filLogs = new ArrayList<FilteredLog>();
		FilteredLog flog = new FilteredLog();
		int index=-1;
		
		if(logs!=null && !logs.isEmpty() && logs.get(0)!=null){
			flog = new FilteredLog();
			flog.setLog(logs.get(0));
			flog.setStartTime(logs.get(0).getTs());
			if(logs.get(0).getEndTs()!=null)
				flog.setEndTime(logs.get(0).getEndTs());
			else
				flog.setEndTime(logs.get(0).getTs());
			filLogs.add(flog);
			index++;
			
		}
		else
			return "--Not Present--";
		
		for(int i=1;i<logs.size(); i++ ){
			
			Log temp = logs.get(i);
			
			if( filLogs.get(index).getLog().giveAPName().replaceFirst("\\d+$", "").equals(
					temp.giveAPName().replaceFirst("\\d+$", ""))){
				
				filLogs.get(index).setStartTime(temp.getTs());
			}
			else{
				flog = new FilteredLog();
				flog.setLog(temp);
				flog.setStartTime(temp.getTs());
				if(temp.getEndTs()!=null)
					flog.setEndTime(temp.getEndTs());
				else
					flog.setEndTime(temp.getTs());
				filLogs.add(flog);
				index++;
				
			}
				
		}
		
		TimeSpent ts = new TimeSpent();
		
		System.out.println("filtered results");
		for (int j = 0; j < filLogs.size() ; j++){
		
			
			Period p = new Period(new DateTime(filLogs.get(j).getStartTime()),new DateTime(filLogs.get(j).getEndTime()));
			setTimeSpentDuration(ts,filLogs.get(j).getLog(),p);
			
			filLogs.get(j).setDuration(daysHoursMinutes.print(p.normalizedStandard()));
			System.out.println(filLogs.get(j));
		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ts));
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ts);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "--Not Present--";
		}
		
	}
	
	

	private String setTimeSpentDuration(TimeSpent ts, Log log, Period p){
		
		String out = new String();
		String temp = new String();
		String num = new String();
		String msg = log.getcLApName();
		System.out.println(ts.getAcademicBuilding()[0]);
		
		if(msg == null){
			
			ts.getDisconnected()[0] = 
					  (int) (ts.getDisconnected()[0] 
							  + p.toStandardDuration().getMillis());
			return "disconnected";
		}
		
		else{
		
		if(msg.startsWith("BH")){
			  out = "BoysHostel";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getBoysHostel()[Integer.parseInt(num)]= 
					  (int) (ts.getBoysHostel()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
		  }
		  
		  else if(msg.startsWith("OUTDOOR_HS")){
			  out = "OutsideHostel";
			  ts.getOutsideHostel()[0] = 
					  (int) (ts.getOutsideHostel()[0] 
							  + p.toStandardDuration().getMillis());
		  }
		  else if(msg.startsWith("OUTDOOR_LH")){
			  out = "OutsideLibraryBuilding";
			  ts.getOutsideLibrary()[0] = 
					  (int) (ts.getOutsideLibrary()[0] 
							  + p.toStandardDuration().getMillis());
		  }
		  
		  else if(msg.startsWith("ACB")){
			  out = "AcademicBuilding";
			  temp = msg.replaceAll("[^0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getAcademicBuilding()[Integer.parseInt(num)]= 
					  (int) (ts.getAcademicBuilding()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  
		  else if(msg.startsWith("LCB")){
			  out = "LectureBlock";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getLectureBlock()[Integer.parseInt(num)]= 
					  (int) (ts.getLectureBlock()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  else if(msg.startsWith("LB")){
			  out = "LibraryBuilding";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getLibraryBuilding()[Integer.parseInt(num)]= 
					  (int) (ts.getLibraryBuilding()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  else if(msg.startsWith("GH")){
			  out = "GirlsHostel";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getGirlsHostel()[Integer.parseInt(num)]= 
					  (int) (ts.getGirlsHostel()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  
		  else if(msg.startsWith("SRB")){
			  out = "ServiceBlock";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getServiceBlock()[Integer.parseInt(num)]= 
					  (int) (ts.getServiceBlock()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  
		  else if(msg.startsWith("DB")){
			  out = "DiningBlock";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getDiningBlock()[Integer.parseInt(num)]= 
					  (int) (ts.getDiningBlock()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  else if(msg.startsWith("RESB")){
			  out = "FacultyResidence";
			  temp = msg.replaceAll("[^-?0-9]+", " "); 
			  num = temp.trim().split(" ")[0];
			  ts.getFacultyResidence()[Integer.parseInt(num)]= 
					  (int) (ts.getFacultyResidence()[Integer.parseInt(num)] 
							  + p.toStandardDuration().getMillis());
					  	
		  }
		  else {
			  out=msg;
			  num=" ";
		  }
		  //console.log(num);
		  
		  if(!num.isEmpty())
			  out = out.concat("_Floor".concat(num.toString()));
		  
		  return out;
		}
	
	}

	
	// HTTP GET request
	private String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		if (responseCode != 200) {
			return "not 200 :(";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}

}
