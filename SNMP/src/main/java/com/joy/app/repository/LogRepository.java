package com.joy.app.repository;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.joy.app.model.Log;


public interface LogRepository extends MongoRepository<Log, String>, LogRepositoryCustom {
	


}
