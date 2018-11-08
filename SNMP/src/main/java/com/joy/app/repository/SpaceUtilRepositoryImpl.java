package com.joy.app.repository;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;



import com.joy.app.model.SpaceUtil;

public class SpaceUtilRepositoryImpl implements SpaceUtilRepositoryCustom {

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public SpaceUtil findByPlaceAndModify(String place,Date from, Date to, int people,  SpaceUtil su) {
		
		// TODO Auto-generated method stub
		
		
		Criteria criteria = Criteria.where("place").is(place).andOperator(Criteria.where("people").is(people));
		
		
		SpaceUtil ret = mongoTemplate.findAndModify(Query.query(criteria),
				new Update().set("to", to).set("from",from).set("series",su.getSeries()).set("drilldown", su.getDrilldown())
				,new FindAndModifyOptions().upsert(true).returnNew(true)
				,SpaceUtil.class);
		
		
		
		return ret;
	}

	@Override
	public SpaceUtil findByPlaceAndPeople(String place, int people) {
		// TODO Auto-generated method stub
		
		Criteria criteria = Criteria.where("place").is(place).andOperator(Criteria.where("people").is(people));
		
		
		SpaceUtil ret = mongoTemplate.findOne(Query.query(criteria),SpaceUtil.class);
		
		
		
		return ret;
		
	
	}

}
