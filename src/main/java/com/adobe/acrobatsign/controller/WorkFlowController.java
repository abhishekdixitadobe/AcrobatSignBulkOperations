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
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserWorkflows;
import com.adobe.acrobatsign.model.WorkflowDescription;
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

	@RequestMapping(value = Constants.GET_WORKFLOWS, method = RequestMethod.GET)
	public String allWorkflows(Model model) {
		UserWorkflows workflowList = workflowService.getWorkflows();
		model.addAttribute("workflowList", workflowList.getUserWorkflowList());
		return "workflowList";
	}

	@RequestMapping(value = Constants.GET_AGREEMENTS, method = RequestMethod.POST, params = "agreementWithWorkflow")
	public String getAgreementsWithWorkflow(Model model, @RequestParam String userEmail, @RequestParam String startDate,
			@RequestParam String userWorkflow, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		int currentPage = page.orElse(0);
		Integer startIndex = size.orElse(0);
		System.out.println(userWorkflow);
		AgreementForm agreementForm = this.workflowService.agreementWithWorkflow(userEmail, startDate, beforeDate,
				startIndex, userWorkflow);

		int totalAgreements = agreementForm.getTotalAgreements().intValue();

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
				PageRequest.of(currentPage, Integer.parseInt(this.maxLimit)), totalAgreements);

		int totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userEmail", userEmail);
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", agreementForm.getAgreementIdList());
		model.addAttribute("totalAgreements", agreementForm.getTotalAgreements());
		model.addAttribute("nextIndex", agreementForm.getNextIndex());
		model.addAttribute("agreementForm", agreementForm);

		return "agreementWithWorkflow";
	}

	@GetMapping(Constants.GET_AGREEMENTS_WITH_WORKFLOW)
	public String getPaginatedUserAgreements(Model model, @RequestParam String userEmail,
			@RequestParam String startDate, @RequestParam String beforeDate, @RequestParam String userWorkflow,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		Integer startIndex = Integer.parseInt(this.maxLimit) * (page.get() - 1);
		AgreementForm agreementForm = this.workflowService.agreementWithWorkflow(userEmail, startDate, beforeDate,
				startIndex, userWorkflow);

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
				PageRequest.of(page.get() - 1, Integer.parseInt(this.maxLimit)), agreementForm.getTotalAgreements());
		long totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userEmail", userEmail);
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", agreementForm.getAgreementIdList());
		model.addAttribute("totalAgreements", agreementForm.getTotalAgreements());
		if (null != agreementForm.getNextIndex()) {
			model.addAttribute("nextIndex", agreementForm.getNextIndex());
		}
		model.addAttribute("agreementForm", agreementForm);

		return "agreementWithWorkflow";
	}

	@GetMapping(Constants.GET_WORKFLOW_DETAILS)
	public String workflowDetails(Model model, @PathVariable String workflowId) {
		LOGGER.info(Constants.AGREEMENT_CREATED, workflowId);
		WorkflowDescription workflowInfo = this.workflowService.workflowDetails(workflowId);
		List<WorkflowDescription> workflowDescriptionList = new ArrayList<>();
		workflowDescriptionList.add(workflowInfo);
		model.addAttribute("agreementInfo", workflowInfo);
		model.addAttribute("displayName", workflowInfo.getDisplayName());
		model.addAttribute("name", workflowInfo.getName());
		model.addAttribute("scope", workflowInfo.getScope());
		return "workflowDetails";
	}

}
