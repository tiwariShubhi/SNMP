package com.joy.app.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.encryption.AESEncrytion;
import com.joy.app.model.Attendance;
import com.joy.app.model.NamedHashEncryptMac;
import com.joy.app.model.User;

public class UserRepositoryImpl implements UserRepositoryCustom{
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public User deleteHmac(String email, String hmac) {
		// TODO Auto-generated method stub
		hmac = new AESEncrytion().encrypt(hmac);
		User user = userRepository.findByEmail(email);
		List<NamedHashEncryptMac> list = user.getNamedHashEncryptMac();
		
		for(NamedHashEncryptMac item : list){
			if(item.getHmac().equals(hmac)){
				list.remove(item);
				user.setNamedHashEncryptMac(list);
				userRepository.save(user);
				return userRepository.findByEmail(email);
			}
				
		}
			
		return null;
	}


	@Override
	public User addHmac(String email, String hmac, String name, String img) {
		// TODO Auto-generated method stub
		
		hmac = new AESEncrytion().encrypt(hmac);
		
		boolean alreadyExists = false;
		User user;
		if((user = userRepository.findByEmail(email))==null){
			user = new User();
			user.setEmail(email);
			user.setAdmin(false);
			user.setSuper_user(false);
		}
		
		List<NamedHashEncryptMac> list = user.getNamedHashEncryptMac();
		
		for(NamedHashEncryptMac item : list){
			if(item.getHmac().equals(hmac)){
				alreadyExists=true;
			}
		}
			
		if(!alreadyExists){
			NamedHashEncryptMac newEntry = new NamedHashEncryptMac();
			newEntry.setHmac(hmac);
			newEntry.setName(name);
			newEntry.setImg(img);
			
			
			list.add(newEntry);
			user.setNamedHashEncryptMac(list);
			userRepository.save(user);
			return userRepository.findByEmail(email);
		}
				
		return null;
	}


	@Override
	public String getAllHmacsJSON(String email) {
		// TODO Auto-generated method stub
		System.out.println("email= "+email);
		User user;
		if((user = userRepository.findByEmail(email)) != null && !user.getNamedHashEncryptMac().isEmpty()){
			
			
		List<NamedHashEncryptMac> list = user.getNamedHashEncryptMac();
		
		for(NamedHashEncryptMac item : list){
			 item.setHmac(new AESEncrytion().decrypt(item.getHmac()));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			//System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userRepository.findByEmail(email).getNamedHashMac()));
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user.getNamedHashEncryptMac());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "--Not Present--";
		}
		}
		else
			return "NoSavedHashedMacs";
	}


	@Override
	public String findHmacJSON(String email, String hmac) {
		// TODO Auto-generated method stub
		
		hmac = new AESEncrytion().encrypt(hmac);
		User user;
		if((user = userRepository.findByEmail(email)) != null && !user.getNamedHashEncryptMac().isEmpty()){
		
			ObjectMapper mapper = new ObjectMapper();
			List<NamedHashEncryptMac> list = user.getNamedHashEncryptMac();
			
			
			for(NamedHashEncryptMac item : list){
				if(item.getHmac().equals(hmac)){
				
					try {
						
						return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		
		return "HashedMacNotFound";
		}
		else
			return "HashedMacNotFound";
		
		
	}
	
	@Override
	public NamedHashEncryptMac findHmac(String email, String hmac) {
		// TODO Auto-generated method stub
		
		hmac = new AESEncrytion().encrypt(hmac);
		User user;
		if((user = userRepository.findByEmail(email)) != null && !user.getNamedHashEncryptMac().isEmpty()){
		
			
			List<NamedHashEncryptMac> list = user.getNamedHashEncryptMac();
			
			
			for(NamedHashEncryptMac item : list){
				if(item.getHmac().equals(hmac)){
				
					return item;
				}
			}
		
		return null;
		}
		else
			return null;
		
		
	}


	@Override
	public User addAdmin(String email) {
		// TODO Auto-generated method stub
		User user;
		if((user = userRepository.findByEmail(email))==null){
			user = new User();
			user.setEmail(email);
			
		}
		user.setAdmin(true);
		
		userRepository.save(user);
		
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User addSuper(String email) {
		// TODO Auto-generated method stub
		User user;
		if((user = userRepository.findByEmail(email))==null){
			user = new User();
			user.setEmail(email);
			
		}
		user.setSuper_user(true);
		
		userRepository.save(user);
		
		return userRepository.findByEmail(email);
	}
	
	
	
}
