package com.joy.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joy.app.model.NamedHashEncryptMac;
import com.joy.app.model.User;
import com.joy.app.repository.UserRepository;


@Controller
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value = "/alluser", method = RequestMethod.GET)
	public @ResponseBody List<User> getAllUser(
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null 
				)
			return null;
		

		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		
		
		return userRepository.findAll();
		
	}
	
	
	@RequestMapping(value = "/gethmac", method = RequestMethod.GET)
	public @ResponseBody String getUser(
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		return userRepository.getAllHmacsJSON((String) session.getAttribute("email"));
	}
	
	@RequestMapping(value = "/addhmac", method = RequestMethod.GET)
	public @ResponseBody User addUser(
			
			@RequestParam(value = "hashedmac") String hmac,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "img", required = false) String img,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		return userRepository.addHmac((String) session.getAttribute("email"), hmac, name, img);
	}
	
	@RequestMapping(value = "/admin/addbyemail", method = RequestMethod.GET)
	public @ResponseBody String addAdmin(
			
			@RequestParam(value = "email") String email,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		if(userRepository.addAdmin(email)!=null){
			System.out.println( email+" is Added Successfully as admin by "+ (String) session.getAttribute("email"));
			
			return "Added Successfully";
		}
		
		return null;
	}
	
	@RequestMapping(value = "/admin/addsuperbyemail", method = RequestMethod.GET)
	public @ResponseBody String addSuper(
			
			@RequestParam(value = "email") String email,
			
			HttpSession session) {
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		if(userRepository.addSuper(email)!=null){
			System.out.println( email+" is Added Successfully as power user by "+ (String) session.getAttribute("email"));
			
			return "Added Successfully";
		}
		
		return null;
	}
	
	
	@RequestMapping(value = "/deletehmac", method = RequestMethod.GET)
	public @ResponseBody User deleteUser(
			
			@RequestParam(value = "hashedmac") String hmac,
			HttpSession session) {
		
		
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		return userRepository.deleteHmac((String) session.getAttribute("email"), hmac);
		
	}
	
	@RequestMapping(value = "/hmacinfo", method = RequestMethod.GET)
	public @ResponseBody String hmacInfo(
			
			@RequestParam(value = "hashedmac") String hmac,
			HttpSession session) {
		
		
		
		if(session.getAttribute("id_token")==null)
			return null;
		
		return userRepository.findHmacJSON((String) session.getAttribute("email"), hmac);
		
	}
	
	
}