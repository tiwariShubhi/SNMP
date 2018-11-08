package com.joy.app.controller;



import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.charts.SpaceUtilDataObject;
import com.joy.app.charts.SpaceUtilDrilldownObject;
import com.joy.app.charts.SpaceUtilDrilldownDataObject;
import com.joy.app.charts.SpaceUtilDrilldownMonthObject;
import com.joy.app.charts.SpaceUtilSeriesObject;
import com.joy.app.charts.WeeklyAcadAreaDataObject;
import com.joy.app.charts.WeeklyAcadAreaSeriesObject;
import com.joy.app.cronJobs.SpaceUtilDBUpdate;
import com.joy.app.model.FilteredLog;
import com.joy.app.model.Log;
import com.joy.app.model.SpaceUtil;
import com.joy.app.repository.LogRepository;
import com.joy.app.repository.SpaceUtilRepository;



@Controller
public class ChartsController {
	
	/**
	 * Controller for display, update charts data 
	 */

	@Autowired
	LogRepository logRepository;
	@Autowired
	SpaceUtilRepository spaceUtilRepository;
	@Autowired
	SpaceUtilDBUpdate spaceUtilDBUpdate;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	public int findExisting(List<WeeklyAcadAreaSeriesObject> wlogs, Log log){
		
		//System.out.println("size wlogs "+wlogs.size());
		
		for(int i=0; i < wlogs.size(); i++){
			
			if(log.giveAPNameOrDisconnected().replaceFirst("\\d+$", "").equals(
					wlogs.get(i).getName())){
				//System.out.println("found"+i);
				return i;
			}
		}
		
		return -1;
		
	}
	
	
	
	@RequestMapping(value = "/acadarea", method = RequestMethod.GET)
	public String getWeeklyAcadArea(
			Model model,
			@RequestParam(value = "hashedmac") String hmac,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		
		return "weekly_acad_area";
		
		
	}
	
	
	@RequestMapping(value = "/data/weeklyacadarea", method = RequestMethod.GET)
	public @ResponseBody String trackUser(
			@RequestParam(value = "hashedmac") String hmac,
			@RequestParam(value = "date") String date,
			HttpSession session) {
		
		
		if(session.getAttribute("id_token")==null)
			return null;
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		//org.joda.time.DateTime midnight	= new org.joda.time.DateTime(2016, 3, 14, 15, 9, 0, 0);
		org.joda.time.DateTime midnight	= new org.joda.time.DateTime(new Date(Long.parseLong(date)));
		midnight = midnight.withTimeAtStartOfDay();
		
		Date from =  midnight.toDate();
		Date to = midnight.plusDays(1).toDate();
				
		System.out.println("from "+from+ " to"+ to);
		String json=null;
		
		//List<Log> logs = logRepository.findByHashedMacDuration(hmac, from, to);
		List<Log> logs = logRepository.findByHashedMacDurationEndTs(hmac, from, to);
		
		/*for (int j = 0; j < logs.size() ; j++){
			
			//FilteredLog temp = filLogs.get(i);
			
			System.out.println(logs.get(j));
		}
		*/
		List<WeeklyAcadAreaSeriesObject> weekLogs = new ArrayList<WeeklyAcadAreaSeriesObject>();
		WeeklyAcadAreaSeriesObject wlog = new WeeklyAcadAreaSeriesObject();
		WeeklyAcadAreaDataObject obj = new WeeklyAcadAreaDataObject();
		
		
		int ind;
		
		for(int i=0;i<logs.size(); i++ ){
			Log temp = logs.get(i);
			if(temp.getEndTs()==null)			/*Ignore the trap with no end timestamp */
				continue;
			
			ind = findExisting(weekLogs, temp);
			//System.out.println("index "+ind);
			if(ind == -1){
				wlog = new WeeklyAcadAreaSeriesObject();
				//TODO
				wlog.setName(temp.giveAPNameOrDisconnected().replaceFirst("\\d+$", ""));
				obj = new WeeklyAcadAreaDataObject();
				obj.setX(0);
				obj.setLow(temp.getTs());
				obj.setHigh(temp.getEndTs());
				
				wlog.getData().add(obj);
				weekLogs.add(wlog);
			}
			else{
				obj = new WeeklyAcadAreaDataObject();
				obj.setX(0);
				obj.setLow(temp.getTs());
				obj.setHigh(temp.getEndTs());
				
				int j = weekLogs.get(ind).getData().size()-1;
				
				if(weekLogs.get(ind).getData().get(j).getLow().compareTo(obj.getHigh())==0
						&& weekLogs.get(ind).getData().get(j).getX()==obj.getX()){
					weekLogs.get(ind).getData().get(j).setLow(obj.getLow());
				}
				else
					weekLogs.get(ind).getData().add(obj);
			}
			
		}
		
		
		
		System.out.println("Weekly timeline chart data received");
		/*for (int j = 0; j < weekLogs.size() ; j++){
		
			//FilteredLog temp = filLogs.get(i);
			
			System.out.println(weekLogs.get(j));
		}*/

		//durations.add(daysHoursMinutes.print(new Period(new DateTime(logs.get(logs.size()-1).getTs()),savedDate).normalizedStandard()));
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(weekLogs);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
		
	String getDayOfMonthSuffix(int n) {
		    
		    if (n >= 11 && n <= 13) {
		        return n+"th";
		    }
		    switch (n % 10) {
		        case 1:  return n+"st";
		        case 2:  return n+"nd";
		        case 3:  return n+"rd";
		        default: return n+"th";
		    }
		}
		
	
	@RequestMapping(value = "/admin/spaceutil", method = RequestMethod.GET)
	public String getSpaceUtil(
			Model model,
			@RequestParam(value = "place") String place,
			@RequestParam(value = "people") int people,
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return "login";
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			model.addAttribute("code", "Not Authorized");
			return "errorpage";
		}
		
		
		
		return "space_util";
		
	}
	
	@RequestMapping(value = "admin/data/spaceutil", method = RequestMethod.GET)
	public @ResponseBody String dataSpaceUtil(
			@RequestParam(value = "place") String place,
			@RequestParam(value = "people") int people,
			HttpSession session) {
		
		

		if(session.getAttribute("id_token")==null)
			return null;
		

		if(!(boolean)session.getAttribute("admin") && !(boolean)session.getAttribute("super")){
			
			return null;
		}
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(spaceUtilRepository.findByPlaceAndPeople(place, people));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
		
	}
	
	@RequestMapping(value = "admin/update/spaceutil/people", method = RequestMethod.GET)

	public @ResponseBody String spaceUtilpeople(

			
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to,
			@RequestParam(value = "people") int people,
			HttpSession session) {
		
		

		if(session.getAttribute("id_token")==null)
			return null;
		

		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		

		String[] buildings = {"LCB","ACB","LB","BH","GH","SRB","DB","OUTDOOR_HS","OUTDOOR_LH"};
  		int[] max_floors = {2,5,3,6,6,2,3,0,0};

		
  		for (int i=0;i<9;i++){
			
			for(int floor=0; floor<=max_floors[i]; floor++){
				

					try{
					updateSpaceUtilDB((max_floors[i]==0?buildings[i]:buildings[i]+floor), from, to, people);
					System.out.println("Succesfully added "+(max_floors[i]==0?buildings[i]:buildings[i]+floor)+ " from "+ from+" to "+to+" people"+people);
					}
					catch(org.springframework.dao.DataAccessResourceFailureException e){
						e.printStackTrace();
					}

			}
		}
		return "Succesfully added ";
			
		
	}
	
	@RequestMapping(value = "admin/update/spaceutil/location-people", method = RequestMethod.GET)
	public @ResponseBody String spaceUtilLocationPeople(
			@RequestParam(value = "place") String place,
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to,
			@RequestParam(value = "people") int people,
			HttpSession session) {
		
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		
		updateSpaceUtilDB(place, from, to, people);
		System.out.println("Succesfully added "+place+ " from "+ from+" to "+to+" people"+people);
		
		
		return "Succesfully added ";
			
		
	}
	
	@RequestMapping(value = "admin/update/spaceutil/dailyupdate", method = RequestMethod.GET)
	public @ResponseBody String spaceUtilDailyUpdate(
			
			HttpSession session) {
		
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		
		spaceUtilDBUpdate.UpdateDB();
		System.out.println("Succesfully completed daily update");
		
		
		return "Succesfully added ";
			
		
	}
	
	
	public String updateSpaceUtilDB(String place, String from, String to, int people ){

		org.joda.time.DateTime from_dt	= new org.joda.time.DateTime(new Date(Long.parseLong(from)))
												.withTimeAtStartOfDay();
		
		org.joda.time.DateTime to_dt	= new org.joda.time.DateTime(new Date(Long.parseLong(to)))
												.withTimeAtStartOfDay();

		Date dfrom =  from_dt.toDate();
		Date dto = to_dt.plusDays(1).toDate();
				
		log.info("space util for ");
		System.out.println(place +" &people "+people+" from "+dfrom+ " to"+ dto);
		
		String json=null;
		
		LocalDate from_ld = Instant.ofEpochMilli(Long.parseLong(from)).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate to_ld = Instant.ofEpochMilli(Long.parseLong(to)).atZone(ZoneId.systemDefault()).toLocalDate();
		long monthsDiff = ChronoUnit.MONTHS.between(from_ld, to_ld);
		//System.out.println("spacd util from "+from_ld+ " to"+ to_ld);
		//System.out.println("monthsdiff"+monthsDiff);
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d""yyyy-MM-dd HH:mm");
		
		
		List<SpaceUtilSeriesObject> series = new ArrayList<SpaceUtilSeriesObject>();
		//List<SpaceUtilDataObject> data = new ArrayList<SpaceUtilDataObject>();
		
		
		
		for(int i=1;i<=31;i++){
			
			SpaceUtilSeriesObject temp = new SpaceUtilSeriesObject();
			temp.setName(getDayOfMonthSuffix(i));
			
			//String hourName = (i<=9?"0":"")+ i+"00 - "+ (i+1<=9?"0":"")+(i+1)+"00 hours";
			
			LocalDate counter = from_ld.withDayOfMonth(1);
			
			for(;counter.compareTo(to_ld.withDayOfMonth(1))<=0;counter = counter.plusMonths(1)){
				
				SpaceUtilDataObject mon = new SpaceUtilDataObject();
				mon.setName(counter.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+counter.getYear());
				mon.setDrilldown(false);
				temp.getData().add(mon);
				
			}
			
			
			series.add(temp);
			
		}
		
		/*for(SpaceUtilSeriesObject t : series){
			System.out.println(t);
		}
		*/
		SpaceUtilDrilldownObject drilldown = new SpaceUtilDrilldownObject();
		
		for(int i=0; i<=23; i++){
			
			Map<String, SpaceUtilDrilldownMonthObject> map = new HashMap<String, SpaceUtilDrilldownMonthObject>();
			
			String hourName = (i<=9?"0":"")+ i+"00 - "+ (i+1<=9?"0":"")+(i+1)+"00 hours";
			
			LocalDate counter = from_ld.withDayOfMonth(1);
			
			for(;counter.compareTo(to_ld.withDayOfMonth(1))<=0;counter = counter.plusMonths(1)){
				
				SpaceUtilDrilldownMonthObject mon = new SpaceUtilDrilldownMonthObject();
				mon.setName(hourName);
				List<SpaceUtilDrilldownDataObject> data = mon.getData();
				
				for(int d = 0; d<counter.lengthOfMonth(); d++){
					
					
					data.add(new SpaceUtilDrilldownDataObject(counter.plusDays(d).toString()));
				}
				
				map.put(counter.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+counter.getYear(), mon);
				
			}
			drilldown.getDrilldown().add(map);
			/*ObjectMapper mapper = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(map);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//System.out.println(json);
			
			
			
		}
		
		
		/*ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(drilldown.getDrilldown());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//System.out.println(json);
		
		
		//List<Log> logs = logRepository.findByHashedMacDuration(hmac, from, to);
		List<Log> logs = logRepository.findByLocationDurationEndTsPeopleFilter(place, dfrom, dto, people);
		log.info("data received");
		System.out.println(place +" &people "+people+" from "+dfrom+ " to"+ dto);
		
				
		/*
		long daysDiff = ChronoUnit.DAYS.between(from_ld, to_ld);
		LocalDate counter = from_ld;
		
		for (int i=0 ; i < daysDiff; i++, counter = counter.plusDays(1)){
			
		}
		*/
		
		for (int j = 0; j < logs.size() ; j++){
			
			//FilteredLog temp = filLogs.get(i);
			Log temp;
			temp = logs.get(j);
			//System.out.println(temp);
			
			LocalDateTime ts_ldt = null;
			LocalDateTime endts_ldt = null;
			
			if(temp.getTs().compareTo(dfrom)>=0 && temp.getEndTs().compareTo(dto)<=0){
				//trap in between from & to
				//System.out.println("trap in between from & to");
				ts_ldt = LocalDateTime.ofInstant(temp.getTs().toInstant(), ZoneId.systemDefault());
				endts_ldt = LocalDateTime.ofInstant(temp.getEndTs().toInstant(), ZoneId.systemDefault());
				
			}
			else if(temp.getTs().compareTo(dfrom)<0 && temp.getEndTs().compareTo(dto)<=0){
				//trap starting before from and ending before to
				
				//System.out.println("trap starting before from and ending before to");
				ts_ldt = LocalDateTime.ofInstant(dfrom.toInstant(), ZoneId.systemDefault());
				endts_ldt = LocalDateTime.ofInstant(temp.getEndTs().toInstant(), ZoneId.systemDefault());
				
				
			}
			else if(temp.getTs().compareTo(dfrom)>=0 && temp.getEndTs().compareTo(dto)>0){
				//trap starting after from and ending after to
				
				//System.out.println("trap starting after from and ending after to");
				ts_ldt = LocalDateTime.ofInstant(temp.getTs().toInstant(), ZoneId.systemDefault());
				endts_ldt = LocalDateTime.ofInstant(dto.toInstant(), ZoneId.systemDefault());
				
				
			}
			else if(temp.getTs().compareTo(dfrom)<0 && temp.getEndTs().compareTo(dto)>0){
				//trap starting before from and ending after to
				//System.out.println("trap starting before from and ending after to");

				ts_ldt = LocalDateTime.ofInstant(dfrom.toInstant(), ZoneId.systemDefault());
				endts_ldt = LocalDateTime.ofInstant(dto.toInstant(), ZoneId.systemDefault());
				
				
			}
			
			long hoursdiff = ChronoUnit.HOURS.between(ts_ldt,endts_ldt);
			
			
			if(ts_ldt.getHour() - endts_ldt.getHour()==0 && hoursdiff ==0  ){
				
				int add_min = (int) ChronoUnit.MINUTES.between(ts_ldt,endts_ldt); 
				
				SpaceUtilDrilldownDataObject data_temp = drilldown.getDrilldown().get(ts_ldt.getHour())
					.get(ts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ts_ldt.getYear()).getData()
						.get(ts_ldt.getDayOfMonth()-1);
				
				data_temp.setY(data_temp.getY()+add_min);
				//System.out.println("-> "+temp + "mins" + drilldown.getDrilldown().get(ts_ldt.getHour())
				//	.get(ts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ts_ldt.getYear()).getData()
				//		.get(ts_ldt.getDayOfMonth()-1));
				
				
			
			}
			else{
				
				//System.out.println("-> "+temp );
				int add_min = 60 - ts_ldt.getMinute(); 
				
				SpaceUtilDrilldownDataObject data_temp = drilldown.getDrilldown().get(ts_ldt.getHour())
					.get(ts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ts_ldt.getYear()).getData()
						.get(ts_ldt.getDayOfMonth()-1);
				
				data_temp.setY(data_temp.getY()+add_min);
				//System.out.println(data_temp);
				
				for(LocalDateTime i = ts_ldt.withMinute(0).withSecond(0).plusHours(1); 
						ChronoUnit.HOURS.between(i,endts_ldt)>0 ;
							i=i.plusHours(1)){
					data_temp = drilldown.getDrilldown().get(i.getHour())
							.get(i.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+i.getYear()).getData()
								.get(i.getDayOfMonth()-1);
					
					data_temp.setY(data_temp.getY()+60);
					//System.out.println(data_temp);
					
				}
				
				data_temp = drilldown.getDrilldown().get(endts_ldt.getHour())
						.get(endts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+endts_ldt.getYear()).getData()
							.get(endts_ldt.getDayOfMonth()-1);
					
				data_temp.setY(data_temp.getY()+endts_ldt.getMinute());
				//System.out.println(data_temp);
				
			}
			
			
			//Populating parent data
			
			long daysdiff = ChronoUnit.DAYS.between(ts_ldt,endts_ldt);
			
			
			if(ts_ldt.getDayOfMonth() - endts_ldt.getDayOfMonth()==0 && daysdiff ==0 ){
				
				int add_min = (int) ChronoUnit.MINUTES.between(ts_ldt,endts_ldt);
				
				SpaceUtilDataObject data_temp = series.get(ts_ldt.getDayOfMonth()-1).getData()
						.get((int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate()));
						
				data_temp.setY(data_temp.getY()+add_min);
				
				if(add_min!=0)data_temp.setDrilldown(true);
				//System.out.println("->"+temp);
				//System.out.println(data_temp +" "+ ts_ldt.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld,ts_ldt) );
				
				//System.out.println("-> "+temp + "mins" + drilldown.getDrilldown().get(ts_ldt.getHour())
				//	.get(ts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ts_ldt.getYear()).getData()
				//		.get(ts_ldt.getDayOfMonth()-1));
				
				
					
			}
			else{
				
				//System.out.println("-> "+temp );
				
				LocalDateTime eod = ts_ldt.with(LocalTime.MAX);
				int add_min = (int) ChronoUnit.MINUTES.between(ts_ldt,eod);
				
				SpaceUtilDataObject data_temp = series.get(ts_ldt.getDayOfMonth()-1).getData()
						.get((int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate()));
						
				data_temp.setY(data_temp.getY()+add_min);
				data_temp.setDrilldown(true);
				//System.out.println(data_temp +" "+ ts_ldt.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate())  +" "+ add_min);
				
				for(LocalDateTime i = ts_ldt.plusDays(1).with(LocalTime.MIN); 
						ChronoUnit.DAYS.between(i,endts_ldt)>0;
							i=i.plusDays(1)){
					data_temp = series.get(i.getDayOfMonth()-1).getData()
							.get((int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),i.toLocalDate()));
							
					data_temp.setY(data_temp.getY()+(60*24));
					data_temp.setDrilldown(true);
					//System.out.println(data_temp +" "+ i.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),i.toLocalDate())  +" "+ 60*24);
					
					
				}
				
				add_min = (int) ChronoUnit.MINUTES.between(endts_ldt.with(LocalTime.MIN),endts_ldt);
				data_temp = series.get(endts_ldt.getDayOfMonth()-1).getData()
						.get((int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),endts_ldt.toLocalDate()));
						
				data_temp.setY(data_temp.getY()+add_min);
				if(add_min!=0)data_temp.setDrilldown(true);
				
				
				//System.out.println(data_temp +" "+ endts_ldt.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),endts_ldt.toLocalDate())+" "+ add_min );
				
			}
			
			
			
		}
		
		
		
		
		//System.out.println();
		
		/*
		
		
		for(SpaceUtilSeriesObject t : series){
			System.out.println(t);
		}
		*/
		

		
		SpaceUtil spaceUtil = new SpaceUtil();
		spaceUtil.setSeries(series);
		spaceUtil.setDrilldown(drilldown);
		
		spaceUtil = spaceUtilRepository.findByPlaceAndModify(place, dfrom, to_dt.toDate(),people, spaceUtil);
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(spaceUtil);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
		
		
		
	
	}
	
}
