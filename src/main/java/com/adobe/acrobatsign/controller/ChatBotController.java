package com.adobe.acrobatsign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@Autowired
	ChatBotService chatBotService;

	@GetMapping("/chat")
	public String chat() {
		return "chat.html";
	}

	@MessageMapping("/chat")
	@SendTo("/topic/chat")
	public Greeting greeting(BotMessage message) throws Exception {
		String obj = null;
		try {
			System.out.println(message.getMessage());			
			obj = this.chatBotService.chatWithGpt3(message.getMessage());

		} catch (Exception e) {
			e.printStackTrace();			
		}
		//return new Greeting(HtmlUtils.htmlEscape(message.getMessage()) + "!");
		return new Greeting(HtmlUtils.htmlEscape(obj));
	}
}
