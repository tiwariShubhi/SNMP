package com.joy.app.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




import com.joy.app.model.Log;
import com.joy.app.repository.LogRepository;

@Controller
public class TestController {

	@Autowired
	LogRepository logRepository;
	
	/**
	 * 
	 * 
	 * @param hash
	 * @param before
	 * @param session
	 * @return
	 * 
	 * 
	 * Useless
	 * 
	 */
	@RequestMapping(value = "/test/1", method = RequestMethod.GET)
	public @ResponseBody Log findlastendtsnulllog(
			@RequestParam(value = "hashedmac") String hash,
			@RequestParam(value = "before") String before,
			HttpSession session) {
				
		

		if(!(boolean)session.getAttribute("admin")){
			
			return null;
		}
		
		
		
		//return logRepository.findLastNullEndTsLog(hash, new Date(Long.parseLong(before)));
		return null;
	}
	
	
}
