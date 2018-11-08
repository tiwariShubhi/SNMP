package com.joy.app.repository;

import com.joy.app.model.NamedHashEncryptMac;
import com.joy.app.model.User;

public interface UserRepositoryCustom {

	
	public User deleteHmac(String email, String hmac);
	
	public User addHmac(String email, String hmac, String name, String img);
	
	public String getAllHmacsJSON(String email);
	
	public String findHmacJSON(String email, String hmac);

	public NamedHashEncryptMac findHmac(String email, String hmac);
	
	public User addAdmin(String email);

	public User addSuper(String email);
}
