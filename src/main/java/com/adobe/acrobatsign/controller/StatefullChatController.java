package com.adobe.acrobatsign.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.util.Constants;

@Controller
public class StatefullChatController {
	
	@RequestMapping(value = Constants.GET_CONVERSATION, method = RequestMethod.GET)
	public void downloadWebforms(HttpServletResponse response, @PathVariable Long conversations) {
		System.out.println("conversation--"+conversations);
	}

}
