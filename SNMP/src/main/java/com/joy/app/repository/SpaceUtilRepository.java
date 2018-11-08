package com.joy.app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.joy.app.model.SpaceUtil;



public interface SpaceUtilRepository extends MongoRepository<SpaceUtil, String>, SpaceUtilRepositoryCustom{
	
	public List<SpaceUtil> findAll();
	
}
