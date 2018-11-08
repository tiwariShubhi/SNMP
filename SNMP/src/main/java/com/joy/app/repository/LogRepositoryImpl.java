package com.joy.app.repository;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.joy.app.model.FootfallData;
import com.joy.app.model.Log;
import com.mongodb.WriteResult;

public class LogRepositoryImpl implements LogRepositoryCustom{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Log> findLatest50() {
		// TODO Auto-generated method stub
		
		
		return mongoTemplate.find(new Query().with(new Sort(new Order(Direction.ASC,"ts"))).limit(5), Log.class);
	}

	

	@Override
	public List<Log> findByHashedMac(String hash) {
		// TODO Auto-generated method stub
		
		
		/*Criteria criteria = new Criteria().orOperator(
				Criteria.where("cldcClientMacAddress").is(hash),
				new Criteria().andOperator(
						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						Criteria.where("bsnStationMacAddress").is(hash)
						)
				);
				
				*/

		Criteria criteria = new Criteria()
		
		.orOperator(
				new Criteria().andOperator(
						Criteria.where("cldcClientMacAddress").is(hash),
						Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState")
						
						),
				new Criteria().andOperator(
						Criteria.where("bsnStationMacAddress").is(hash),

						Criteria.where("oid").is("bsnDot11StationDisassociate")
						)
						
			);

		
		
		List<Log> results = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))).limit(500),Log.class);
		//System.out.println("printing results");
		
		
		return results;
		
		
	}
	

	@Override
	public List<Log> findByMultipleHashedMac(String hash1, String hash2) {
		// TODO Auto-generated method stub
		
		
		/*Criteria criteria = new Criteria().orOperator(
				Criteria.where("cldcClientMacAddress").is(hash),
				new Criteria().andOperator(
						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						Criteria.where("bsnStationMacAddress").is(hash)
						)
				);
				
				*/

		Criteria criteria = new Criteria()
		
		.orOperator(
				new Criteria().andOperator(
						new Criteria().orOperator(
								Criteria.where("cldcClientMacAddress").is(hash1),
								Criteria.where("cldcClientMacAddress").is(hash2)
								),
						Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState")
						
						),
				new Criteria().andOperator(
						new Criteria().orOperator(
								Criteria.where("bsnStationMacAddress").is(hash1),
								Criteria.where("bsnStationMacAddress").is(hash2)
								),
						Criteria.where("oid").is("bsnDot11StationDisassociate")
						)
						
			);

		
		
		List<Log> results = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))).limit(500),Log.class);
		//System.out.println("printing results");
		
		
		return results;
		
		
	}
	
	
	
	@Override
	public List<Log> findByMultipleHashedMacDuration(String hash1, String hash2, Date from, Date to) {
		// TODO Auto-generated method stub
		
		
		/*Criteria criteria = new Criteria().orOperator(
				Criteria.where("cldcClientMacAddress").is(hash),
				new Criteria().andOperator(
						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						Criteria.where("bsnStationMacAddress").is(hash)
						)
				);
				
				*/

		Criteria criteria = new Criteria()
		
		.orOperator(
				new Criteria().andOperator(
						new Criteria().orOperator(
								Criteria.where("cldcClientMacAddress").is(hash1),
								Criteria.where("cldcClientMacAddress").is(hash2)
								),
						Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
						
						Criteria.where("ts").gte(from),
						Criteria.where("ts").lt(to)
						
						),
				new Criteria().andOperator(
						new Criteria().orOperator(
								Criteria.where("bsnStationMacAddress").is(hash1),
								Criteria.where("bsnStationMacAddress").is(hash2)
								),
						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						
						Criteria.where("ts").gte(from),
						Criteria.where("ts").lt(to)
						)
						
			);

		
		
		List<Log> results = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))).limit(500),Log.class);
		//System.out.println("printing results");
		
		return results;
		
		
	}
	
	
	
	@Override
	public List<Log> findByHashedMacDuration(String hash, Date from, Date to) {
		// TODO Auto-generated method stub
		
		
		/*Criteria criteria = new Criteria().andOperator(new Criteria().orOperator(
				Criteria.where("cldcClientMacAddress").is(hash),
				new Criteria().andOperator(
						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						Criteria.where("bsnStationMacAddress").is(hash)
						)
				),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				);
		*/
		Criteria criteria = new Criteria()
		
		.orOperator(
				new Criteria().andOperator(
						Criteria.where("cldcClientMacAddress").is(hash),
						Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
						
						Criteria.where("ts").gte(from),
						Criteria.where("ts").lt(to)
						
						),
				new Criteria().andOperator(
						Criteria.where("bsnStationMacAddress").is(hash),

						Criteria.where("oid").is("bsnDot11StationDisassociate"),
						
						Criteria.where("ts").gte(from),
						Criteria.where("ts").lt(to)
						)
						
			);

		
		
		
		
		List<Log> results = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))),Log.class);
		//System.out.println("printing results");
		/*for(Log log:results){
			System.out.println(log.toJson());
		}*/
		
		return results;
		
		
	}
	
	@Override
	public List<Log> findByHashedMacDurationEndTs(String hash, Date from,
			Date to) {
		
		Criteria criteria = new Criteria()
		
			.orOperator(
					new Criteria().andOperator(
							Criteria.where("cldcClientMacAddress").is(hash),
							Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
							
							Criteria.where("endTs").gte(from),
							Criteria.where("ts").lt(to)
							
							),
					new Criteria().andOperator(
							Criteria.where("bsnStationMacAddress").is(hash),
	
							Criteria.where("oid").is("bsnDot11StationDisassociate"),
							
							Criteria.where("endTs").gte(from),
							Criteria.where("ts").lt(to)
							)
							
				);

		
		
		
		
		List<Log> results = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))),Log.class);
		//System.out.println("printing results");
		/*for(Log log:results){
			System.out.println(log.toJson());
		}
		*/
		return results;
		
		
	}
	

	
	@Override
	public List<Log> findByLocationDurationEndTsPeopleFilter(String place,
			Date from, Date to, int people) {
		// TODO Auto-generated method stub
		
		Criteria criteria = null;
		
		switch(people){
				
		
			case 1:
				criteria = 
					new Criteria().andOperator(
							Criteria.where("cLApName").regex("^("+place+")"),
							Criteria.where("cldcClientSSID").is("STUDENTS-M "),
							
							Criteria.where("endTs").gte(from),
							Criteria.where("ts").lt(to)
							
							);
					break;
					
			case 2:
				criteria = 
					new Criteria().andOperator(
							Criteria.where("cLApName").regex("^("+place+")"),
							Criteria.where("cldcClientSSID").is("FACULTY-STAFF-N "),
							
							Criteria.where("endTs").gte(from),
							Criteria.where("ts").lt(to)
							
							);
				break;
			
			case 3:
				criteria = 
					new Criteria().andOperator(
							Criteria.where("cLApName").regex("^("+place+")"),
							Criteria.where("cldcClientSSID").is("GUEST-N "),
							
							Criteria.where("endTs").gte(from),
							Criteria.where("ts").lt(to)
							
							);
				break;
				
			case 0:
				criteria = 
						new Criteria().andOperator(
								Criteria.where("cLApName").regex("^("+place+")"),
								Criteria.where("cldcClientSSID").regex("^(G|F|STUDENTS-M)"),
								
								Criteria.where("endTs").gte(from),
								Criteria.where("ts").lt(to)
								
								);
				break;
		}

	List<Log> results = mongoTemplate.find(Query.query(criteria),Log.class);
		//System.out.println("printing results");
		/*for(Log log:results){
			System.out.println(log.toJson());
		}
		*/
		return results;
	
		
	}


	
	
	@Override
	public Log firstInAcademicAreaToday(String hash) {
		// TODO Auto-generated method stub
		//Pattern p = new Pattern(/^(ACB)/);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().atStartOfDay();
		Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
		
		
		Criteria c = Criteria.where("cldcClientMacAddress").is(hash)
				.andOperator(Criteria.where("cLApName").regex("^((ACB|LCB|LB))").andOperator(Criteria.where("ts").gte(d1))
				);
		
		
		return mongoTemplate.findOne(Query.query(c).with(new Sort(new Order(Direction.ASC,"ts"))), Log.class);
	}

	

	@Override
	public Date avgAcademicAreaEntry(String hash) {
		// TODO Auto-generated method stub
		
		List<Long> ret = new ArrayList<Long>();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().atStartOfDay();
		LocalDateTime temp = now.toLocalDate().atStartOfDay();
		Log t = null;
		Date d1= new Date(),d2=new Date();
		Criteria c = null;
		
		for( int i=0;i<5;){
			
			temp = temp.minusDays(1);
			if(temp.getDayOfWeek().getValue()<=5){
				i++;
				//System.out.println(temp);
				d1 = Date.from(temp.atZone(ZoneId.systemDefault()).toInstant());
				d2 = Date.from(temp.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
				
				c = Criteria.where("cldcClientMacAddress").is(hash)
						.andOperator(
								Criteria.where("cLApName").regex("^((ACB|LCB|LB))")
								.andOperator(Criteria.where("ts").gte(d1)
										.andOperator(Criteria.where("ts").lt(d2)))
						);
				
				t = mongoTemplate.findOne(Query.query(c).with(new Sort(new Order(Direction.ASC,"ts"))), Log.class);
				
				if(t != null){
					System.out.println(t.getTs().getTime()+" - "+d1.getTime());
					ret.add(t.getTs().getTime()-d1.getTime());
				
				}
			}
		
		}
		
		long sum=0;
		for (Long d : ret){
			System.out.println(d.toString()+"one");
			sum+=d;
			
		}
		
			
		if (ret.size()!=0){
			System.out.println(sum/ret.size());
			Date d= new Date(Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant()).getTime() + (sum/ret.size()));
			return d;
			
		}
		
		return null;
		/*Date d3 = new Date();
		return d3.getHours()+":"+d3.getMinutes()+":"+d3.getSeconds();
		*/
	}
	
	/*@Override
	public long avgAcademicHours(String hash) {
		// TODO Auto-generated method stub
		
		long total;
		LocalDateTime from = LocalDateTime.now().minusDays(1).with(LocalTime.MIN);
		LocalDateTime to = LocalDateTime.now().with(LocalTime.MIN);
		
		List<Log> logs = new ArrayList<Log>();
		Date d1= new Date(),d2=new Date();
		Criteria c = null;
		
		for( int i=0;i<5;from = from.minusDays(1), to = to.minusDays(1)){
			
			
			if(from.getDayOfWeek().getValue()<=5){
				i++;
				//System.out.println(temp);
				d1 = Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
				d2 = Date.from(to.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
				
				c = Criteria.where("cldcClientMacAddress").is(hash)
						.andOperator(
								Criteria.where("cLApName").regex("^((ACB|LCB|LB))")
								.andOperator(Criteria.where("ts").gte(d1)
										.andOperator(Criteria.where("ts").lt(d2)))
						);
				
				logs = mongoTemplate.find(Query.query(c).with(new Sort(new Order(Direction.ASC,"ts"))), Log.class);
				
					
					for (Log log : logs){
						
						 if(log.getcLApName()!=null && log.getcLApName().matches("^(ACB|LB|LCB)(.*)"))
						 {
							 //System.out.println(log);
							 Period p = new Period(new DateTime(log.getTs()),new DateTime(log.getEndTs()!=null?log.getEndTs():log.getTs()));
								
							 total += p.toStandardDuration().getMillis();
							
						 }
					}
				}
				
		}
		
		
		
		
	}
*/
	
	
	@Override
	public Log exitAcademicArea(String hash) {
		// TODO Auto-generated method stub
		
		/* TODO:
		 * get schedule of the hash
		 * find the last log of acad area
		 * timestamp of next log is the exit time
		*/
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().atStartOfDay();
		LocalDateTime temp = now.toLocalDate().atStartOfDay().minusDays(1);
		
		Date d1 = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
		Date d2 = Date.from(temp.atZone(ZoneId.systemDefault()).toInstant());
		
		
		Criteria c = Criteria.where("cldcClientMacAddress").is(hash)
				.andOperator(Criteria.where("cLApName").regex("^((ACB|LCB|LB))")
						.andOperator(Criteria.where("ts").gt(d2)
								.andOperator(Criteria.where("ts").lte(d1)))
				);
		
		
		return mongoTemplate.findOne(Query.query(c).with(new Sort(new Order(Direction.DESC,"ts"))), Log.class);
	
		
		
		
	}
	
	


	@Override
	public Date avgAcademicAreaExit(String hash) {
		// TODO Auto-generated method stub

		List<Long> ret = new ArrayList<Long>();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().atStartOfDay();
		LocalDateTime temp = now.toLocalDate().atStartOfDay().minusDays(1);
		Log t = null;
		Date d1= new Date(),d2=new Date();
		Criteria c = null;
		
		for( int i=0, j=0;i<15 && j<5;){
			
			temp = temp.minusDays(1);
			
				i++;
				//System.out.println(temp);
				d1 = Date.from(temp.atZone(ZoneId.systemDefault()).toInstant());
				d2 = Date.from(temp.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
				
				c = Criteria.where("cldcClientMacAddress").is(hash)
						.andOperator(Criteria.where("cLApName").regex("^((ACB|LCB|LB))")
								.andOperator(Criteria.where("ts").gt(d1)
										.andOperator(Criteria.where("ts").lte(d2)))
						);
				
				t = mongoTemplate.findOne(Query.query(c).with(new Sort(new Order(Direction.DESC,"ts"))), Log.class);
				
				if(t != null){
					j++;
					System.out.println(t.getTs().getTime()+" - "+d1.getTime()+"avg");
					ret.add(t.getTs().getTime()-d1.getTime());
				
				}
			
		
		}
		
		long sum=0;
		for (Long d : ret){
			System.out.println(d.toString()+"one avg");
			sum+=d;
			
		}
		
			
		if (ret.size()!=0){
			System.out.println(sum/ret.size());
			Date d= new Date(Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant()).getTime() + (sum/ret.size()));
			return d;
			
		}
		
		return null;

		
	}



	@Override
	public Log findAndUpdateLog(String hash, Date before, long daySerial) {
		// TODO Auto-generated method stub
		
		/*LocalDateTime ldt = LocalDateTime.ofInstant(before.toInstant(), ZoneId.systemDefault());
		LocalDateTime midnight = ldt.toLocalDate().atStartOfDay();
		*/
		//logger.info("starting to update");
		Criteria criteria = new Criteria()
		
			.orOperator(
					new Criteria().andOperator(
							Criteria.where("cldcClientMacAddress").is(hash),
							Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							
							),
					new Criteria().andOperator(
							Criteria.where("bsnStationMacAddress").is(hash),
	
							Criteria.where("oid").is("bsnDot11StationDisassociate"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							)
					,
					new Criteria().andOperator(
							Criteria.where("bsnStationMacAddress").is(hash),
	
							Criteria.where("oid").is("bsnDot11StationDeauthenticate"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							)
							
				);

		
		
		
		
		/*Log log = mongoTemplate.findAndModify(Query.query(criteria).with(
				new Sort(new ArrayList<Order>(Arrays.asList(
						new Order(Direction.DESC,"ts"), new Order(Direction.DESC,"daySerialNo"))))).limit(1),
				new Update().set("endTs", before)
				,new FindAndModifyOptions().returnNew(true)
				,Log.class);
		*/
		
		
		Log log = mongoTemplate.findAndModify(Query.query(criteria).with(
				new Sort(new Order(Direction.DESC,"ts"))).limit(1),
				new Update().set("endTs", before)
				,new FindAndModifyOptions().returnNew(true)
				,Log.class);
		
		
		
		//logger.info("completing update");
		return log;
		
	}
	
	@Override
	public Log findLastNullEndTsLog(String hash, Date before, long daySerial) {
		// TODO Auto-generated method stub
		
		/*LocalDateTime ldt = LocalDateTime.ofInstant(before.toInstant(), ZoneId.systemDefault());
		LocalDateTime midnight = ldt.toLocalDate().atStartOfDay();
		*/
		
		Criteria criteria = new Criteria()
		
			.orOperator(
					new Criteria().andOperator(
							Criteria.where("cldcClientMacAddress").is(hash),
							Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							
							),
					new Criteria().andOperator(
							Criteria.where("bsnStationMacAddress").is(hash),
	
							Criteria.where("oid").is("bsnDot11StationDisassociate"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							)
					,
					new Criteria().andOperator(
							Criteria.where("bsnStationMacAddress").is(hash),
	
							Criteria.where("oid").is("bsnDot11StationDeauthenticate"),
							Criteria.where("daySerialNo").ne(daySerial),
							
							Criteria.where("ts").lte(before)
							)
				);

		
		
		
		
		
		Log log = mongoTemplate.findOne(Query.query(criteria).with(new Sort(new Order(Direction.DESC,"ts"))).limit(1)
				
				,Log.class);
		
		if(log!=null){
			Criteria criteria2 = Criteria.where("_id").is(log.getId());
			
			if(log.getEndTs()==null){
				mongoTemplate.updateFirst(Query.query(criteria2), new Update().set("endTs", before),
						
						Log.class);
			}
		}
		
		return log;
		
	}
	
	

	@Override
	public FootfallData footfallData(Date from, Date to) {
		// TODO Auto-generated method stub
		
		FootfallData data = new FootfallData();
		
		data.setFrom(from);
		data.setTo(to);
		
		Criteria c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(LB0)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getLibraryBuilding()[0] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(LB1)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getLibraryBuilding()[1] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(LB2)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getLibraryBuilding()[2] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(LB3)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getLibraryBuilding()[3] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB0)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[0] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB1)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[1] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB2)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[2] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB3)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[3] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB4)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[4] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		c = new Criteria().andOperator(
				Criteria.where("cLApName").regex("^(ACB5)"),
				Criteria.where("oid").is("ciscoLwappDot11ClientMovedToRunState"),
				Criteria.where("cldcClientSSID").is("STUDENTS-M "),
				Criteria.where("ts").gte(from),
				Criteria.where("ts").lt(to)
				
				);
				
				
		
		
		data.getAcademicBuilding()[5] = (int) mongoTemplate.count(Query.query(c), Log.class);
		
		
		
		
		
		return data;
	}



	

	

	
	
}
