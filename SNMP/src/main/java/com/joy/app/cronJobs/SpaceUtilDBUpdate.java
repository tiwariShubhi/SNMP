package com.joy.app.cronJobs;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.charts.SpaceUtilDataObject;
import com.joy.app.charts.SpaceUtilDrilldownObject;
import com.joy.app.charts.SpaceUtilDrilldownDataObject;
import com.joy.app.charts.SpaceUtilDrilldownMonthObject;
import com.joy.app.charts.SpaceUtilSeriesObject;
import com.joy.app.model.Log;
import com.joy.app.model.SpaceUtil;
import com.joy.app.repository.LogRepository;
import com.joy.app.repository.SpaceUtilRepository;

@Component
public class SpaceUtilDBUpdate {
	
	@Autowired
	SpaceUtilRepository spaceUtilRepository;
	@Autowired
	LogRepository logRepository;
	
	
	List<SpaceUtil> chartData = new ArrayList<SpaceUtil>();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 * updates DB with new data from the day
	 * 
	 * 
	 * NOTE: Code repeated at 
	 */
	public void UpdateDB(){
		
		log.info("SpaceUtil DB Update");
		
		LocalDate today = LocalDate.now().minusDays(1);
		chartData = spaceUtilRepository.findAll();
		
		for(SpaceUtil tp : chartData){
			SpaceUtil original = new SpaceUtil();
			ObjectMapper mapper = new ObjectMapper();
			try {
				original = mapper.readValue(mapper.writeValueAsString(tp), SpaceUtil.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LocalDate from_ld = LocalDateTime.ofInstant(tp.getTo().toInstant(), ZoneId.systemDefault()).toLocalDate();
			
			SpaceUtilDrilldownObject drilldown= tp.getDrilldown();
			List<SpaceUtilSeriesObject> series = tp.getSeries();
			LocalDateTime from_ldt = LocalDateTime.ofInstant(tp.getTo().toInstant(), ZoneId.systemDefault()).with(LocalTime.MIN);
			Date dfrom = Date.from(from_ldt.atZone(ZoneId.systemDefault()).toInstant());
			Date dto = Date.from(LocalDateTime.now().with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
			
			System.out.println("from_ld= "+ from_ld.toString() +" i= "+ from_ld.plusMonths(1).withDayOfMonth(1).toString()
					+" today= "+today.toString()+" mod today= "+today.plusMonths(1).withDayOfMonth(1));
			
			for(LocalDate i = from_ld.minusDays(1).plusMonths(1).withDayOfMonth(1); 
					ChronoUnit.MONTHS.between(i,today.plusMonths(1).withDayOfMonth(1))!=0;
						i=i.plusMonths(1)){
				
				SpaceUtilDataObject dobj  =  new SpaceUtilDataObject();
				dobj.setName(i.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+i.getYear());
				dobj.setY(0);
				dobj.setDrilldown(false);
				System.out.println(dobj.toString());
				
				for(SpaceUtilSeriesObject so : series){
					
					so.getData().add(dobj);
				}
				
				for(int index =0 ; index< drilldown.getDrilldown().size();index++){
					String hourName = (index<=9?"0":"")+ i+"00 - "+ (index+1<=9?"0":"")+(index+1)+"00 hours";
					
					
					SpaceUtilDrilldownMonthObject mon = new SpaceUtilDrilldownMonthObject();
					mon.setName(hourName);
					List<SpaceUtilDrilldownDataObject> data = mon.getData();
					
					for(int d = 0; d<i.lengthOfMonth(); d++){
						
						
						data.add(new SpaceUtilDrilldownDataObject(i.plusDays(d).toString()));
					}
					
					drilldown.getDrilldown().get(index)
						.put(i.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+i.getYear(), mon);
					
					
					
				}
			}

			/*for(SpaceUtilSeriesObject x: series){
				System.out.println(x);
			}
			for(SpaceUtilSeriesObject x: t.getSeries()){
				System.out.println(x);
			}
			
			for(Map<String, SpaceUtilDrilldownMonthObject> x: drilldown.getDrilldown()){
				//System.out.println(x);
			}*/
				
				
			String place = tp.getPlace();
			int people = tp.getPeople();
			
			List<Log> logs = logRepository.findByLocationDurationEndTsPeopleFilter(place, dfrom, dto, people);
			log.info("data received");
			System.out.println(place +" &people "+people+" from "+dfrom+ " to"+ dto);
			
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
							.get((original.getSeries().get(ts_ldt.getDayOfMonth()-1).getData().size()-1)+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate()));
							
					data_temp.setY(data_temp.getY()+add_min);
					
					if(add_min!=0)data_temp.setDrilldown(true);
					
					
					//System.out.println("->"+temp);
					//System.out.println(data_temp +" "+ ts_ldt.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld,ts_ldt) );
					//System.out.println(data_temp+" "+ ts_ldt.getDayOfMonth() +" "+ (tp.getSeries().get(ts_ldt.getDayOfMonth()-1).getData().size() - 1)+" "+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate())  +" "+ add_min);
					
					//System.out.println("-> "+temp + "mins" + drilldown.getDrilldown().get(ts_ldt.getHour())
					//	.get(ts_ldt.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+ts_ldt.getYear()).getData()
					//		.get(ts_ldt.getDayOfMonth()-1));
					
					
						
				}
				else{
					
					//System.out.println("-> "+temp );
					
					LocalDateTime eod = ts_ldt.with(LocalTime.MAX);
					int add_min = (int) ChronoUnit.MINUTES.between(ts_ldt,eod);
					
					SpaceUtilDataObject data_temp = series.get(ts_ldt.getDayOfMonth()-1).getData()
							.get((original.getSeries().get(ts_ldt.getDayOfMonth()-1).getData().size()-1)+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate()));
							
					data_temp.setY(data_temp.getY()+add_min);
					data_temp.setDrilldown(true);
					
					//System.out.println(data_temp+" "+ ts_ldt.getDayOfMonth() +" "+ (tp.getSeries().get(ts_ldt.getDayOfMonth()-1).getData().size() - 1)+" "+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),ts_ldt.toLocalDate())  +" "+ add_min);
					
					for(LocalDateTime i = ts_ldt.plusDays(1).with(LocalTime.MIN); 
							ChronoUnit.DAYS.between(i,endts_ldt)>0;
								i=i.plusDays(1)){
						//
						data_temp = series.get(i.getDayOfMonth()-1).getData()
								.get((original.getSeries().get(i.getDayOfMonth()-1).getData().size()-1) +(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),i.toLocalDate()));
								
						data_temp.setY(data_temp.getY()+(60*24));
						data_temp.setDrilldown(true);
						//System.out.println(data_temp+" "+ i.getDayOfMonth() +" "+ (original.getSeries().get(ts_ldt.getDayOfMonth()-1).getData().size() - 1)+" "+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),i.toLocalDate())  +" "+ (60*24));
						
						
					}
					
					add_min = (int) ChronoUnit.MINUTES.between(endts_ldt.with(LocalTime.MIN),endts_ldt);
					data_temp = series.get(endts_ldt.getDayOfMonth()-1).getData()
							.get((original.getSeries().get(endts_ldt.getDayOfMonth()-1).getData().size()-1)+(int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),endts_ldt.toLocalDate()));
							
					data_temp.setY(data_temp.getY()+add_min);
					if(add_min!=0)data_temp.setDrilldown(true);
					
					
					//System.out.println(data_temp +" "+ endts_ldt.getDayOfMonth() +" "+ (int) ChronoUnit.MONTHS.between(from_ld.withDayOfMonth(1),endts_ldt.toLocalDate())+" "+ add_min );
					
				}
				
				
				
			}
			
			tp.setDrilldown(drilldown);
			tp.setSeries(series);
			
			spaceUtilRepository.findByPlaceAndModify(place, tp.getFrom(), dto,people, tp);
			
		}
		
	}
	
	
	
		
}
