package com.adobe.acrobatsign.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import com.adobe.acrobatsign.model.BotMessage;
import com.adobe.acrobatsign.model.Greeting;
import com.adobe.acrobatsign.service.ChatBotService;

@Controller
public class ChatBotController {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotController.class);

	@Autowired
	ChatBotService chatBotService;


	@MessageMapping("/chat")
	@SendTo("/topic/chat")
	public Greeting greeting(BotMessage message) throws Exception {
		String obj = null;
		try {
			LOGGER.info(message.getMessage());
			obj = this.chatBotService.chatWithGpt3(message.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Greeting(HtmlUtils.htmlEscape(obj));
	}
	
	@MessageMapping("/statefullchat")
	@SendTo("/topic/statefullchat")
	public Greeting stateFullChat(BotMessage message) throws Exception {
		String obj = null;
		try {
			LOGGER.info("Inside statefullchat:: "+message.getMessage());
			obj = this.chatBotService.chatWithGpt3(message.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Greeting(HtmlUtils.htmlEscape(obj));
	}
}
