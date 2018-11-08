package com.joy.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NamedHashEncryptMac {
	
	String name;
	String img;
	String hmac;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	
	@Override
	public String toString() {
		return "NamedHashEncryptMac [name=" + name + ", img=" + img + ", hmac="
				+ hmac + "]";
	}
	
	
	
	
	
}
