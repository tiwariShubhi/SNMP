package com.joy.app.repository;

import java.util.Date;

import com.joy.app.model.SpaceUtil;

public interface SpaceUtilRepositoryCustom {


	public SpaceUtil findByPlaceAndModify(String place,Date from, Date to, int people,  SpaceUtil su);
	
	public SpaceUtil findByPlaceAndPeople(String place, int people);
}
