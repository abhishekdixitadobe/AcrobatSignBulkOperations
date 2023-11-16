package com.adobe.acrobatsign.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adobe.acrobatsign.model.Agreement;
import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.AdobeSignService;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ManageAgreementsController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ManageAgreementsController.class);

	@Value(value = "${downloadPath}")
	private String downloadPath;

	final ObjectMapper objectMapper = new ObjectMapper();

	/** The adobe sign service. */
	@Autowired
	AdobeSignService adobeSignService;

	private void deleteJsonFile() {
		try {
			final File directory = new File(downloadPath);
			Path path = Paths.get(directory + "/" + "allAgreements" + ".json");
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
			// Handle error appropriately
		}
	}

	@RequestMapping(value = Constants.MANAGER_MULTI_USER_AGREEMENTS, method = RequestMethod.POST, params = "exportEvents")
	public String exportEvents(Model model, @ModelAttribute("agreementForm") AgreementForm agreementForm) {

		JSONParser parser = new JSONParser();
		List<Agreement> data = null;
		final File directory = new File(downloadPath);
		try {
			data = objectMapper.readValue(new File(directory + "/" + "allAgreements" + ".json"),
					new TypeReference<List<Agreement>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<UserAgreement> userAgreements = null;

		userAgreements = this.adobeSignService.allUserEvents(data);
		AgreementForm form = new AgreementForm();
		form.setAgreementIdList(userAgreements);
		form.setAgreementIdList(userAgreements);

		model.addAttribute(Constants.AGREEMENT_LIST, userAgreements);
		model.addAttribute(Constants.TOTAL_AGREEMENTS, userAgreements.size());
		model.addAttribute(Constants.AGREEMENT_FORM, form);
		model.addAttribute(Constants.USER_WORKFLOW, agreementForm.getUserWorkflow());
		deleteJsonFile();
		return "allUserAgreementList";
	}

}
