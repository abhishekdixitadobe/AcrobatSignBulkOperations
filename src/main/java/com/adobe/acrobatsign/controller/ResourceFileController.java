package com.adobe.acrobatsign.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.adobe.acrobatsign.model.Conversation;
import com.adobe.acrobatsign.model.User;
import com.adobe.acrobatsign.model.UserConversation;
import com.adobe.acrobatsign.repository.UserConversationRepository;
import com.adobe.acrobatsign.repository.UserRepository;
import com.adobe.acrobatsign.service.CustomUserDetails;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.annotations.ApiIgnore;

@Controller
public class ResourceFileController {
	
	@Autowired
	private UserConversationRepository userConversationRepo;
	
	@Autowired
	private UserRepository userRepo;

	/**
	 * Send contract method.
	 *
	 * @return the string
	 */
	@ApiIgnore
	@GetMapping(Constants.SEND_FOR_SIGNATURE_ENDPOINT)
	public String getAgreementPage() {
		return Constants.SEND_FORM_HTML;
	}
	/**
	 * open main page.
	 *
	 * @return the string
	 */
	@ApiIgnore
	@GetMapping(Constants.INDEX_PAGE_ENDPOINT)
	public String indexPage() {
		return Constants.INDEX_HTML;
	}
	
	@GetMapping(Constants.MAIN_PAGE_ENDPOINT)
	public String sendContractMethod() {
		return Constants.LOGIN_PAGE_HTML;
	}

	@GetMapping(Constants.SEND_PAGE_ENDPOINT)
	public String sendPageMethod() {
		return Constants.SEND_FORM_HTML;
	}
	
	
	@GetMapping(Constants.GET_BOT)
	public String botPageMethod() {
		return Constants.GET_BOTX;
	}
	
	private List<Conversation> setConversation(String conversation){
		return null;
	}
	@GetMapping(Constants.GET_SIGN_BOT)
	public String statefullChatPageMethod(Model model) {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		if(null != loggedInUser) {
			CustomUserDetails customUserDetails = (CustomUserDetails) loggedInUser.getPrincipal();
			UserConversation conversations = userConversationRepo.findByUser(customUserDetails.getUser());
			if(null != conversations) {
				String conversationData = conversations.getConversationData();
				ObjectMapper objectMapper = new ObjectMapper();
	            try {
					List<Conversation> chatList = objectMapper.readValue(conversationData, new TypeReference<List<Conversation>>() {});
					model.addAttribute("conversationData", chatList);
	            } catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return Constants.GET_SIGN_BOT_HTML;
	}
	
}
