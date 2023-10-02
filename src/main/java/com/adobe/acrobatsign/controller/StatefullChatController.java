package com.adobe.acrobatsign.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.acrobatsign.model.Conversation;
import com.adobe.acrobatsign.model.ConversationQuery;
import com.adobe.acrobatsign.service.ChatBotService;
import com.adobe.acrobatsign.service.CustomUserDetails;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class StatefullChatController {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatefullChatController.class);
	
	@Autowired
	ChatBotService chatService;
	
	
	@RequestMapping(value = Constants.GET_CONVERSATION, method = RequestMethod.GET)
	public List<ConversationQuery> conversationQueries(Model model, @ModelAttribute String queries , HttpServletResponse response, @PathVariable Long conversations) {
		LOGGER.info("conversation--"+conversations);
		List<ConversationQuery> queryList =  chatService.getQueries(conversations);
		ObjectMapper mapper = new ObjectMapper();
		try {
			queries = mapper.writeValueAsString(queryList);
			model.addAttribute("queries", mapper.writeValueAsString(queries));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}
	
	@RequestMapping(value = Constants.SET_CONVERSATION, method = RequestMethod.POST)
	public Conversation setConversation(Model model, @ModelAttribute String queries , HttpServletResponse response) {
		Conversation conversation = null;
		try {
			conversation = chatService.setConversation();
			Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
			if(null != loggedInUser) {
				CustomUserDetails customUserDetails = (CustomUserDetails) loggedInUser.getPrincipal();
				Map<String, Object> additionalObject = new HashMap<>();
				additionalObject.put("conversations", conversation);
				chatService.updateConversation(customUserDetails,additionalObject);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conversation;
	}

}
