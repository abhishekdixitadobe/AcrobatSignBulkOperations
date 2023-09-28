package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.ExportAgreement;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.model.SelectedAgreement;
import com.adobe.acrobatsign.model.SendAgreementVO;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.AdobeSignService;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * The Class AdobeSignController.
 */
@Controller
public class AdobeSignController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignController.class);

	@Value(value = "${baseUrl}")
	private String baseUrl;

	/** The adobe sign service. */
	@Autowired
	AdobeSignService adobeSignService;

	@Value("${pageSize}")
	public String maxLimit;

	@Value(value = "${downloadPath}")
	private String downloadPath;

	@Value(value = "${integration-key}")
	private String integrationKey;

	@RequestMapping(value = Constants.CANCEL_AGREEMENTS, method = RequestMethod.POST)
	public String cancelAgreements(Model model, @RequestBody List<SelectedAgreement> selectedAgreements) {

		adobeSignService.cancelAgreements(selectedAgreements);
		model.addAttribute("agreementList", selectedAgreements);
		return Constants.CANCEL_HTML;
	}

	@RequestMapping(value = Constants.CANCEL_REMINDERS, method = RequestMethod.POST)
	public String cancelReminders(Model model, @RequestBody List<SelectedAgreement> selectedAgreements) {
		adobeSignService.cancelReminders(selectedAgreements);
		return Constants.BULK_AGREEMENT_HOME_HTML;
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "delete")
	public String deleteAgreements(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		try {
			adobeSignService.deleteAgreements(seletedList(agreementForm), userEmail);
		} catch (Exception e) {
			if (e instanceof HttpClientErrorException
					&& ((HttpClientErrorException) e).getStatusCode() == HttpStatus.FORBIDDEN) {
				model.addAttribute(Constants.ERROR, Constants.FORBIDDEN_ERROR);
				return Constants.ERROR;
			}
			e.printStackTrace();
		}
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.BULK_AGREEMENT_HOME_HTML;
	}

	@RequestMapping(value = "/downloadList", method = RequestMethod.POST)
	public void downloadAgreementList(HttpServletResponse response, @RequestBody List<UserAgreement> selectedAgreements,
			HttpServletRequest request) {
		String filename = "agreement.csv";
		response.setContentType("text/csv");

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		try {
			StatefulBeanToCsv<ExportAgreement> writer = new StatefulBeanToCsvBuilder<ExportAgreement>(
					response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			writer.write(exportList(selectedAgreements));
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = Constants.DOWNLOAD_FORM_FIELDS, method = RequestMethod.POST)
	public ResponseEntity<StreamingResponseBody> downloadformfields(HttpServletResponse response,
			@RequestBody List<SelectedAgreement> selectedAgreements, HttpServletRequest request) {
		final StreamingResponseBody streamResponseBody = out -> {
			adobeSignService.downloadFormFields(selectedAgreements, response);
		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=FormFields.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return ResponseEntity.ok(streamResponseBody);
	}

	@RequestMapping(value = "/downloadAgreements", method = RequestMethod.POST)
	public ResponseEntity<StreamingResponseBody> downloadMultipleAgreements(HttpServletResponse response,
			@RequestBody List<SelectedAgreement> selectedAgreements, HttpServletRequest request) {
		final StreamingResponseBody streamResponseBody = out -> {
			adobeSignService.downloadAgreements(selectedAgreements, response);
		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=agreements.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return ResponseEntity.ok(streamResponseBody);
	}

	private List<ExportAgreement> exportList(List<UserAgreement> agreementList) {
		List<ExportAgreement> csvData = new ArrayList<>();

		for (final UserAgreement userAgreement : agreementList) {
			ExportAgreement exportAgreement = new ExportAgreement();
			exportAgreement.setAgreementId(StringUtils.trim(userAgreement.getId()));
			exportAgreement.setAgreementName(StringUtils.trim(userAgreement.getName()));
			exportAgreement.setStatus(userAgreement.getStatus());
			exportAgreement.setUserEmail(userAgreement.getUserEmail());
			exportAgreement.setModifiedDate(userAgreement.getModifiedDate());
			csvData.add(exportAgreement);
		}

		return csvData;
	}

	@PostMapping(Constants.FETCH_AGREEMENT_FOR_IDS)
	public String fetchAgreementBasedOnIds(Model model, @RequestParam(Constants.PARAM_FILE) MultipartFile file1,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		List<String> agreementIds = new ArrayList<>();
		final AgreementForm agreementForm = new AgreementForm();
		if (!file1.isEmpty()) {
			final byte[] bytes;
			try {
				final InputStream inputStream = file1.getInputStream();
				final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				agreementIds = br.lines().collect(Collectors.toList());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		final List<UserAgreement> agreementIdList = adobeSignService.searchAgreementsForIds(agreementIds);
		agreementForm.setAgreementIdList(agreementIdList);
		model.addAttribute("agreementForm", agreementForm);
		model.addAttribute("agreementIdList", agreementIdList);

		return "agreementInfoList";
	}

	@PostMapping(Constants.FETCH_AGREEMENT)
	public String fetchUsersAgreement(Model model, @RequestParam(Constants.PARAM_FILE) MultipartFile file1,
			@RequestParam String startDate, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		final int currentPage = page.orElse(0);
		final Integer startIndex = size.orElse(0);

		List<String> userIds = new ArrayList<>();
		final AgreementForm agreementForm = new AgreementForm();
		if (!file1.isEmpty()) {
			final byte[] bytes;
			try {
				final InputStream inputStream = file1.getInputStream();
				final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				userIds = br.lines().collect(Collectors.toList());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		LOGGER.info("date", beforeDate);
		final MultiUserAgreementDetails multiUserAgreementDetails = adobeSignService.searchMultiUserAgreements(userIds,
				startDate, beforeDate, startIndex);

		final long totalAgreements = multiUserAgreementDetails.getTotalAgreements();
		agreementForm.setAgreementIdList(multiUserAgreementDetails.getAgreementList());

		final Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(
				multiUserAgreementDetails.getAgreementList(), PageRequest.of(currentPage, Integer.parseInt(maxLimit)),
				totalAgreements);
		final long totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			final List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userIds", multiUserAgreementDetails.getUserEmails());
		if (userIds.size() > 1) {
			model.addAttribute("userEmail", userIds.get(1));
		} else {
			model.addAttribute("userEmail", null);
		}
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			model.addAttribute("nextIndexMap",
					objectMapper.writeValueAsString(multiUserAgreementDetails.getNextIndexMap()));
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", multiUserAgreementDetails.getAgreementList());
		model.addAttribute("totalAgreements", multiUserAgreementDetails.getTotalAgreements());
		model.addAttribute("agreementForm", agreementForm);

		return "multiUserAgreementList";
	}

	@GetMapping(Constants.GET_AGREEMENT_STATUS)
	public String getAgreementIdStatus(Model model, @PathVariable String agreementId) {
		LOGGER.info(Constants.AGREEMENT_CREATED, agreementId);
		final AgreementInfo agreementInfo = adobeSignService.getContractStatus(agreementId);
		final List<AgreementInfo> agreementInfoList = new ArrayList<>();
		agreementInfoList.add(agreementInfo);
		model.addAttribute("agreementInfo", agreementInfo);
		model.addAttribute("senderEmail", agreementInfo.getSenderEmail());
		model.addAttribute("name", agreementInfo.getName());
		model.addAttribute("status", agreementInfo.getStatus());
		model.addAttribute("partcipantSet", agreementInfo.getParticipantSet());
		return "agreementdetails";
	}

	private String getBaseURL() {
		return baseUrl + Constants.BASE_URL_API_V6;
	}

	@PostMapping(Constants.GET_MULTI_USER_AGREEMENTS)
	public String getMultiUserAgreements(Model model, @RequestBody String userIdsJson, @RequestParam String startDate,
			@RequestParam String beforeDate, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") String nextIndexMap) {

		final AgreementForm agreementForm = new AgreementForm();
		Map<String, Integer> nextIndexMapVal = new HashMap<>();
		// Convert JSON to a List<String>
		ObjectMapper userMapper = new ObjectMapper();
		List<String> userEmail = null;
		final ObjectMapper mapper = new ObjectMapper();
		// URL-decode the userIds parameter
		try {
			String decodedUserIds = URLDecoder.decode(userIdsJson, "UTF-8").replace("userIds=", "").replaceAll("\\s+",
					"");
			// decodedUserIds = "[" + decodedUserIds + "]";

			nextIndexMapVal = mapper.readValue(nextIndexMap, HashMap.class);
			userEmail = userMapper.readValue(decodedUserIds, List.class);
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final MultiUserAgreementDetails multiUserAgreementDetails = adobeSignService
				.searchMultiUserAgreements(userEmail, startDate, beforeDate, "ABC", nextIndexMapVal, page.orElse(0));

		final long totalAgreements = multiUserAgreementDetails.getTotalAgreements();
		agreementForm.setAgreementIdList(multiUserAgreementDetails.getAgreementList());

		final Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(
				multiUserAgreementDetails.getAgreementList(),
				PageRequest.of(page.get() - 1, Integer.parseInt(maxLimit)), totalAgreements);
		final long totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			final List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userIds", multiUserAgreementDetails.getUserEmails());
		if (userEmail.size() > 1) {
			model.addAttribute("userEmail", userEmail.get(1));
		} else {
			model.addAttribute("userEmail", null);
		}
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			model.addAttribute("nextIndexMap",
					objectMapper.writeValueAsString(multiUserAgreementDetails.getNextIndexMap()));
		} catch (final JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", multiUserAgreementDetails.getAgreementList());
		model.addAttribute("totalAgreements", multiUserAgreementDetails.getTotalAgreements());
		model.addAttribute("agreementForm", agreementForm);

		return "multiUserAgreementList";
	}

	@GetMapping(Constants.GET_AGREEMENTS)
	public String getPaginatedUserAgreements(Model model,
			@RequestParam(value = "userEmail", required = false) String userEmail, @RequestParam String startDate,
			@RequestParam String beforeDate, @RequestParam(value = "userGroup", required = false) String userGroup,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		final Integer startIndex = Integer.parseInt(maxLimit) * (page.get() - 1);
		final AgreementForm agreementForm = adobeSignService.searchAgreements(userEmail, startDate, beforeDate,
				startIndex, userGroup);
		if (null != agreementForm && null != agreementForm.getTotalAgreements()
				&& null != agreementForm.getAgreementIdList() && agreementForm.getAgreementIdList().size() > 0) {
			final Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
					PageRequest.of(page.get() - 1, Integer.parseInt(maxLimit)), agreementForm.getTotalAgreements());
			final long totalPages = agreementPage.getTotalPages();
			if (totalPages > 0) {
				final List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed()
						.collect(Collectors.toList());
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
		}

		return "agreementList";
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "reminders")
	public String getReminders(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {

		List<String> events;
		events = adobeSignService.getReminders(seletedList(agreementForm), userEmail);
		model.addAttribute("events", events);
		model.addAttribute("totalReminders", events.size());
		if (events.size() > 0) {
			return Constants.REMINDER_HTML;
		}
		return Constants.NODATA_HTML;
	}

	@RequestMapping(value = Constants.GET_AGREEMENTS, method = RequestMethod.POST, params = "agreements")
	public String getUserAgreements(Model model, @RequestParam String userEmail, @RequestParam String startDate,
			@RequestParam String beforeDate, @RequestParam String userGroup,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		final int currentPage = page.orElse(0);
		final Integer startIndex = size.orElse(0);
		boolean showNoData = true;

		final AgreementForm agreementForm = adobeSignService.searchAgreements(userEmail, startDate, beforeDate,
				startIndex, userGroup);
		if (null != agreementForm && null != agreementForm.getTotalAgreements()
				&& null != agreementForm.getAgreementIdList() && agreementForm.getAgreementIdList().size() > 0) {
			final int totalAgreements = agreementForm.getTotalAgreements().intValue();
			if (totalAgreements > 0) {
				showNoData = false;
			}

			final Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
					PageRequest.of(currentPage, Integer.parseInt(maxLimit)), totalAgreements);

			final int totalPages = agreementPage.getTotalPages();
			if (totalPages > 0) {
				final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed()
						.collect(Collectors.toList());
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
		}

		if (showNoData) {
			return "noData";
		}
		return "agreementList";
	}

	@RequestMapping(value = Constants.HIDE_AGREEMENTS, method = RequestMethod.POST)
	public String hideAgreements(Model model, @RequestBody List<SelectedAgreement> selectedAgreements) {
		adobeSignService.hideAgreements(selectedAgreements);
		return "agreementList";
	}

	private List<UserAgreement> seletedAgreementList(List<UserAgreement> agreementList) {
		final List<UserAgreement> seletedList = new ArrayList<>();
		for (final UserAgreement agreement : agreementList) {
			if (agreement.getIsChecked() != null) {
				seletedList.add(agreement);
			}
		}
		return seletedList;
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

	/**
	 * Send for signature.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @param file1           the file 1
	 * @param name            the name
	 * @param signerEmail     the signer email
	 * @param message         the message
	 * @param approverEmail   the approver email
	 * @return the string
	 */
	@PostMapping(Constants.SEND_AGREEMENT)
	public ResponseEntity<String> sendAgreement(@RequestParam(Constants.PARAM_FILE) MultipartFile file1,
			@RequestParam String data) {
		final JSONParser parser = new JSONParser();
		org.json.JSONArray jsonArray = null;
		try {
			final Object object = parser.parse(data);
			jsonArray = new org.json.JSONArray(object.toString());

		} catch (final ParseException e) {
			e.printStackTrace();
		}
		LOGGER.info("data", data);
		final String agreementId = adobeSignService.sendAgreement(jsonArray, file1);
		// List<UserAgreement> agreementList = this.adobeSignService.getAgreements();
		return new ResponseEntity<String>(agreementId, HttpStatus.OK);
	}

	/**
	 * Send for signature.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @param file1           the file 1
	 * @param name            the name
	 * @param signerEmail     the signer email
	 * @param message         the message
	 * @param approverEmail   the approver email
	 * @return the string
	 */
	@PostMapping(Constants.SEND_FOR_SIGNATURE_ENDPOINT)
	public String sendForSignature(Model model, SendAgreementVO sendAgreementVO,
			@RequestParam(Constants.PARAM_FILE) MultipartFile file1, @RequestParam String name,
			@RequestParam String signerEmail, @RequestParam String message, @RequestParam String approverEmail) {
		sendAgreementVO.setSignerEmail(signerEmail);
		sendAgreementVO.setApproverEmail(approverEmail);
		sendAgreementVO.setMessage(message);
		sendAgreementVO.setName(name);
		final String agreementId = adobeSignService.sendContract(sendAgreementVO, file1);
		final List<UserAgreement> agreementList = adobeSignService.getAgreements(null);
		model.addAttribute("agreementList", agreementList);
		return Constants.SEND_FORM_HTML;
	}
}