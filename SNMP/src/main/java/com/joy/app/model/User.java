package com.joy.app.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class User {
	
	
	
	
	@Id
	private String email;
	
	private List<NamedHashEncryptMac> namedHashEncryptMac = new ArrayList<NamedHashEncryptMac>();
	
	boolean admin;
	
	boolean super_user;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<NamedHashEncryptMac> getNamedHashEncryptMac() {
		return namedHashEncryptMac;
	}
	public void setNamedHashEncryptMac(List<NamedHashEncryptMac> namedHashEncryptMac) {
		this.namedHashEncryptMac = namedHashEncryptMac;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isSuper_user() {
		return super_user;
	}
	public void setSuper_user(boolean super_user) {
		this.super_user = super_user;
	}
	
	
	
}
