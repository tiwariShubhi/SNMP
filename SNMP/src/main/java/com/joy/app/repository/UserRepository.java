package com.joy.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.joy.app.model.User;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	
	
	public User findByEmail(String email);
	
	
}
