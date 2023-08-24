package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.adobe.acrobatsign.model.Agreement;
import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.ExportWorkflowAgreement;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserWorkflows;
import com.adobe.acrobatsign.model.WorkflowDescription;
import com.adobe.acrobatsign.service.AdobeSignService;
import com.adobe.acrobatsign.service.UserService;
import com.adobe.acrobatsign.service.WorkflowService;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Controller
public class WorkFlowController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowController.class);

	@Value("${pageSize}")
	public String maxLimit;

	/** The adobe sign service. */
	@Autowired
	WorkflowService workflowService;

	@Autowired
	UserService userService;

	/** The adobe sign service. */
	@Autowired
	AdobeSignService adobeSignService;

	@Value(value = "${downloadPath}")
	private String downloadPath;

	final ObjectMapper objectMapper = new ObjectMapper();

	@RequestMapping(value = Constants.WORKFLOWS_FOR_AGREEMENTS, method = RequestMethod.POST)
	public String agreementsWithWorkflow(Model model, @ModelAttribute("agreementForm") AgreementForm agreementForm) {

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

		userAgreements = this.workflowService.workflowForAgreements(data, agreementForm.getUserWorkflow());
		AgreementForm form = new AgreementForm();
		form.setAgreementIdList(userAgreements);
		form.setAgreementIdList(userAgreements);

		model.addAttribute(Constants.AGREEMENT_LIST, userAgreements);
		model.addAttribute(Constants.TOTAL_AGREEMENTS, userAgreements.size());
		model.addAttribute(Constants.AGREEMENT_FORM, form);
		model.addAttribute(Constants.USER_WORKFLOW, agreementForm.getUserWorkflow());
		deleteJsonFile();
		return "workflowsForAgreement";
	}

	@RequestMapping(value = Constants.GET_AGREEMENTS, method = RequestMethod.POST)
	public String allAgreements(Model model, @RequestParam String startDate,
			@RequestParam(Constants.PARAM_FILE) MultipartFile file1, @RequestParam String userWorkflow,
			@RequestParam String beforeDate, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size, HttpSession session) {

		final int currentPage = page.orElse(0);
		final Integer startIndex = size.orElse(0);
		deleteJsonFile();
		List<String> activeUserList = new ArrayList<>();

		if (!file1.isEmpty()) {
			final byte[] bytes;
			try {
				final InputStream inputStream = file1.getInputStream();
				final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				activeUserList = br.lines().collect(Collectors.toList());
				activeUserList.remove(0);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		List<JSONArray> agreementForm = this.workflowService.agreementWithWorkflow(activeUserList, startDate,
				beforeDate, startIndex, userWorkflow);

		Long totalSize = (long) agreementForm.size();

		AgreementForm form = new AgreementForm();
		form.setUserWorkflow(userWorkflow);

		final File directory = new File(downloadPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		try {
			objectMapper.writeValue(new File(directory + "/" + "allAgreements" + ".json"), agreementForm);
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute(Constants.AGREEMENT_LIST, agreementForm);
		model.addAttribute(Constants.TOTAL_AGREEMENTS, totalSize);
		model.addAttribute(Constants.AGREEMENT_FORM, form);
		model.addAttribute(Constants.USER_WORKFLOW, userWorkflow);

		return "agreementWithWorkflow";
	}

	@RequestMapping(value = Constants.GET_WORKFLOWS, method = RequestMethod.GET)
	public String allWorkflows(Model model) {
		UserWorkflows workflowList = workflowService.getWorkflows();
		model.addAttribute(Constants.WORKFLOW_LIST, workflowList.getUserWorkflowList());
		return "workflowList";
	}

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

	@RequestMapping(value = Constants.DOWNLOAD_WORKFLOW_AGREEMENTS, method = RequestMethod.POST, params = "download")
	public void downloadList(HttpServletResponse response, @ModelAttribute("agreementForm") AgreementForm agreementForm,
			@RequestParam String agreementList) {

		String filename = "agreement.csv";
		response.setContentType("text/csv");

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		try {
			StatefulBeanToCsv<ExportWorkflowAgreement> writer = new StatefulBeanToCsvBuilder<ExportWorkflowAgreement>(
					response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			writer.write(seletedList(agreementForm.getAgreementIdList()));
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			e.printStackTrace();
		}
	}

	private List<UserAgreement> seletedList(AgreementForm agreementForm) {
		final List<UserAgreement> seletedList = new ArrayList<>();
		for (final UserAgreement agreement : agreementForm.getAgreementIdList()) {
			if (agreement.getIsChecked() != null) {
				seletedList.add(agreement);
			}
		}
		return seletedList;
	}

	private List<ExportWorkflowAgreement> seletedList(List<UserAgreement> agreementList) {
		List<ExportWorkflowAgreement> exportWorkflowList = new ArrayList<>();
		for (final UserAgreement userAgreement : agreementList) {
			ExportWorkflowAgreement exportWorkflow = new ExportWorkflowAgreement();
			exportWorkflow.setAgreementId(userAgreement.getId());
			exportWorkflow.setAgreementName(userAgreement.getName());
			exportWorkflow.setStatus(userAgreement.getStatus());
			exportWorkflow.setWorkflowName(userAgreement.getWorkflowName());
			exportWorkflowList.add(exportWorkflow);
		}
		return exportWorkflowList;
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
