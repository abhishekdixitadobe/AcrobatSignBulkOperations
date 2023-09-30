package com.adobe.acrobatsign.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.model.Conversation;
import com.adobe.acrobatsign.model.Message;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class StatefullChatController {
	
	@RequestMapping(value = Constants.GET_CONVERSATION, method = RequestMethod.GET)
	public void downloadWebforms(HttpServletResponse response, @PathVariable Long conversations) {
		
		Conversation conversation = new Conversation();
		conversation.setConversation_id(1294324);
		conversation.setConversation_name("stateful_conversation_746529f6-53af-4193-8b53-ec90368a5b67");
		
		Message messages = new Message();
		messages.setContent("You are an AI assistant that helps people find information.");
		messages.setRole("user");
		
		System.out.println("conversation--"+conversations);
	}

}
