package com.adobe.acrobatsign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.adobe.acrobatsign.repository.UserConversationRepository;
import com.adobe.acrobatsign.repository.UserRepository;
import com.adobe.acrobatsign.util.Constants;

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
	@GetMapping(Constants.GET_SIGN_BOT)
	public String statefullChatPageMethod(Model model) {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		if(null != loggedInUser) {
			//userConversationRepo.findByUser(null)
		}
		return Constants.GET_SIGN_BOT_HTML;
	}
	
}
