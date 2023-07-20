package com.adobe.acrobatsign.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.DetailedUserInfo;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserWorkflows;
import com.adobe.acrobatsign.model.WorkflowDescription;
import com.adobe.acrobatsign.service.UserService;
import com.adobe.acrobatsign.service.WorkflowService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class WorkFlowController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignController.class);

	@Value("${pageSize}")
	public String maxLimit;

	/** The adobe sign service. */
	@Autowired
	WorkflowService workflowService;

	@Autowired
	UserService userService;

	@RequestMapping(value = Constants.GET_AGREEMENTS, method = RequestMethod.POST, params = "agreementWithWorkflow")
	public String agreementsWithWorkflow(Model model, @RequestParam String startDate,
			@RequestParam List<String> activeUserList, @RequestParam String userWorkflow,
			@RequestParam String beforeDate, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		final int currentPage = page.orElse(0);
		final Integer startIndex = size.orElse(0);

		AgreementForm agreementForm = this.workflowService.agreementWithWorkflow(activeUserList, startDate, beforeDate,
				startIndex, userWorkflow);

		int totalAgreements = agreementForm.getTotalAgreements().intValue();

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
				PageRequest.of(currentPage, Integer.parseInt(this.maxLimit)), totalAgreements);

		int totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute(Constants.PAGE_NUMBERS, pageNumbers);
		}

		// model.addAttribute("userEmail", activeUserList);
		model.addAttribute(Constants.USER_WORKFLOW, userWorkflow);
		model.addAttribute(Constants.START_DATE, startDate);
		model.addAttribute(Constants.BEFORE_DATE, beforeDate);
		model.addAttribute(Constants.AGREEMENT_PAGE, agreementPage);
		model.addAttribute(Constants.AGREEMENT_LIST, agreementForm.getAgreementIdList());
		model.addAttribute(Constants.TOTAL_AGREEMENTS, agreementForm.getTotalAgreements());
		model.addAttribute(Constants.NEXT_INDEX, agreementForm.getNextIndex());
		model.addAttribute(Constants.AGREEMENT_FORM, agreementForm);

		return "agreementWithWorkflow";
	}

	@RequestMapping(value = Constants.GET_WORKFLOWS, method = RequestMethod.GET)
	public String allWorkflows(Model model) {
		UserWorkflows workflowList = workflowService.getWorkflows();
		List<DetailedUserInfo> activeUserList = userService.activeUsers();
		model.addAttribute(Constants.WORKFLOW_LIST, workflowList.getUserWorkflowList());
		model.addAttribute(Constants.ACTIVE_USER_LIST, activeUserList);
		return "workflowList";
	}

	@GetMapping(Constants.GET_WORKFLOW_DETAILS)
	public String workflowDetails(Model model, @PathVariable String workflowId) {
		LOGGER.info(Constants.AGREEMENT_CREATED, workflowId);
		WorkflowDescription workflowInfo = this.workflowService.workflowDetails(workflowId);
		List<WorkflowDescription> workflowDescriptionList = new ArrayList<>();
		workflowDescriptionList.add(workflowInfo);
		model.addAttribute(Constants.AGREEMENT_INFO, workflowInfo);
		model.addAttribute(Constants.DISPLAY_NAME, workflowInfo.getDisplayName());
		model.addAttribute(Constants.NAME, workflowInfo.getName());
		model.addAttribute(Constants.SCOPE, workflowInfo.getScope());
		return "workflowDetails";
	}

}
