package com.adobe.acrobatsign.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.service.ChatBotService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class StatefullChatController {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatefullChatController.class);
	
	@Autowired
	ChatBotService chatService;
	
	@RequestMapping(value = Constants.GET_CONVERSATION, method = RequestMethod.GET)
	public void downloadWebforms(HttpServletResponse response, @PathVariable Long conversations) {
		LOGGER.info("conversation--"+conversations);
		chatService.getQueries(conversations);
		
	}

}
