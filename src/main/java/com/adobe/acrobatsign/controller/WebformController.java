package com.adobe.acrobatsign.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.MultiUserWidgetDetails;
import com.adobe.acrobatsign.service.UserService;
import com.adobe.acrobatsign.service.WebformService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class WebformController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignController.class);

	@Value("${pageSize}")
	public String maxLimit;

	/** The adobe sign service. */
	@Autowired
	WebformService webformService;

	@Autowired
	UserService userService;

	@RequestMapping(value = Constants.GET_WIDGET, method = RequestMethod.GET)
	public String allWorkflows(Model model) {
		AgreementForm agreementForm = new AgreementForm();

		MultiUserWidgetDetails multiUserWidgetDetails = webformService.getWebforms();
		model.addAttribute("widgetList", multiUserWidgetDetails.getWidgetList());
		model.addAttribute("activeUserList", multiUserWidgetDetails.getUserEmails());
		model.addAttribute("agreementForm", agreementForm);
		return "webformList";
	}

}
