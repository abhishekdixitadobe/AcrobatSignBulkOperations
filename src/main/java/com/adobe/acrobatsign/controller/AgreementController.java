package com.adobe.acrobatsign.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.model.UserGroups;
import com.adobe.acrobatsign.service.GroupService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class AgreementController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignController.class);

	@Value("${pageSize}")
	public String maxLimit;	
	
	@Autowired
	GroupService groupService;	

	@RequestMapping(value = Constants.BULK_AGREEMENT_HOME_PAGE_ENDPOINT, method = RequestMethod.GET)
	public String allGroups(Model model) {
		
		UserGroups userGroupList = groupService.getGroups();
		model.addAttribute(Constants.GROUP_LIST, userGroupList.getGroupInfoList());
		return "form";
	}
}
