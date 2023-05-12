package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.model.SendAgreementVO;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.AdobeSignService;
import com.adobe.acrobatsign.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class AdobeSignController.
 */
@Controller
public class AdobeSignController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignController.class);

	/** The adobe sign service. */
	@Autowired
	AdobeSignService adobeSignService;

	@Value("${pageSize}")
	public String maxLimit;

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "cancel")
	public String cancelReminders(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.cancelReminders(this.seletedList(agreementForm), userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.LOGIN_HTML;
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "delete")
	public String deleteAgreements(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.deleteAgreements(this.seletedList(agreementForm), userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.LOGIN_HTML;
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "download")
	public ResponseEntity<StreamingResponseBody> downloadAgreements(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm) {
		StreamingResponseBody streamResponseBody = out -> {
			this.adobeSignService.downloadAgreements(this.seletedList(agreementForm), userEmail, response);
		};
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=agreements.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return new ResponseEntity(streamResponseBody, HttpStatus.OK);
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "formfield")
	public ResponseEntity<StreamingResponseBody> downloadformfields(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm,
			HttpServletRequest request) {
		StreamingResponseBody streamResponseBody = out -> {
			this.adobeSignService.downloadFormFields(this.seletedList(agreementForm), userEmail, response);
		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=FormFields.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return ResponseEntity.ok(streamResponseBody);
	}

	@PostMapping(Constants.FETCH_AGREEMENT)
	public String fetchUsersAgreement(Model model, @RequestParam(Constants.PARAM_FILE) MultipartFile file1,
			@RequestParam String startDate, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		int currentPage = page.orElse(0);
		Integer startIndex = size.orElse(0);

		List<String> userIds = new ArrayList<>();
		AgreementForm agreementForm = new AgreementForm();
		if (!file1.isEmpty()) {
			byte[] bytes;
			try {
				InputStream inputStream = file1.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				userIds = br.lines().collect(Collectors.toList());
				// userIds = Arrays.asList(columns);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOGGER.info("date", beforeDate);
		MultiUserAgreementDetails multiUserAgreementDetails = this.adobeSignService.searchMultiUserAgreements(userIds,
				startDate, beforeDate, startIndex);

		long totalAgreements = multiUserAgreementDetails.getTotalAgreements();

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(multiUserAgreementDetails.getAgreementList(),
				PageRequest.of(currentPage, Integer.parseInt(this.maxLimit)), totalAgreements);
		long totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userIds", multiUserAgreementDetails.getUserEmails());
		if (userIds.size() > 1) {
			model.addAttribute("userEmail", userIds.get(1));
		} else {
			model.addAttribute("userEmail", null);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			model.addAttribute("nextIndexMap",
					objectMapper.writeValueAsString(multiUserAgreementDetails.getNextIndexMap()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", multiUserAgreementDetails.getAgreementList());
		model.addAttribute("totalAgreements", multiUserAgreementDetails.getTotalAgreements());
		model.addAttribute("agreementForm", agreementForm);

		// if(null != agreementForm.getNextIndex()) {
		// model.addAttribute("nextIndex", agreementForm.getNextIndex());
		// }
		// model.addAttribute("agreementForm", agreementForm);

		return "multiUserAgreementList";
	}

	@GetMapping(Constants.GET_AGREEMENT_STATUS)
	public String getAgreementIdStatus(Model model, @PathVariable String agreementId) {
		LOGGER.info(Constants.AGREEMENT_CREATED, agreementId);
		AgreementInfo agreementInfo = this.adobeSignService.getContractStatus(agreementId);
		List<AgreementInfo> agreementInfoList = new ArrayList<>();
		agreementInfoList.add(agreementInfo);
		model.addAttribute("agreementInfo", agreementInfo);
		model.addAttribute("senderEmail", agreementInfo.getSenderEmail());
		model.addAttribute("name", agreementInfo.getName());
		model.addAttribute("status", agreementInfo.getStatus());
		model.addAttribute("partcipantSet", agreementInfo.getParticipantSet());
		return "agreementdetails";
	}

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

	@GetMapping(Constants.GET_MULTI_USER_AGREEMENTS)
	public String getMultiUserAgreements(Model model, @RequestParam List<String> userEmail,
			@RequestParam String startDate, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") String nextIndexMap) {

		AgreementForm agreementForm = new AgreementForm();
		Map<String, Integer> nextIndexMapVal = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			nextIndexMapVal = mapper.readValue(nextIndexMap, HashMap.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		MultiUserAgreementDetails multiUserAgreementDetails = this.adobeSignService.searchMultiUserAgreements(userEmail,
				startDate, beforeDate, nextIndexMapVal);

		long totalAgreements = multiUserAgreementDetails.getTotalAgreements();

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(multiUserAgreementDetails.getAgreementList(),
				PageRequest.of(0, Integer.parseInt(this.maxLimit)), totalAgreements);
		long totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userIds", multiUserAgreementDetails.getUserEmails());
		if (userEmail.size() > 1) {
			model.addAttribute("userEmail", userEmail.get(1));
		} else {
			model.addAttribute("userEmail", null);
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("beforeDate", beforeDate);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementList", multiUserAgreementDetails.getAgreementList());
		model.addAttribute("totalAgreements", multiUserAgreementDetails.getTotalAgreements());
		model.addAttribute("agreementForm", agreementForm);
		model.addAttribute("nextIndexMap", multiUserAgreementDetails.getNextIndexMap());

		return "multiUserAgreementList";
	}

	@GetMapping(Constants.GET_AGREEMENTS)
	public String getPaginatedUserAgreements(Model model, @RequestParam String userEmail,
			@RequestParam String startDate, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = 50;

		AgreementForm agreementForm = this.adobeSignService.searchAgreements(userEmail, startDate, beforeDate,
				size.get());

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(agreementForm.getAgreementIdList(),
				PageRequest.of(currentPage, pageSize), agreementForm.getTotalAgreements());

		int totalPages = agreementPage.getTotalPages();
		if (agreementForm.getNextIndex() == null) {
			totalPages = currentPage;
		}
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
		if (null != agreementForm.getNextIndex()) {
			model.addAttribute("nextIndex", agreementForm.getNextIndex());
		}
		model.addAttribute("agreementForm", agreementForm);

		return "agreementList";
	}

	@PostMapping(Constants.GET_AGREEMENTS)
	public String getUserAgreements(Model model, @RequestParam String userEmail, @RequestParam String startDate,
			@RequestParam String beforeDate, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		// List<UserAgreement> agreementList =
		// this.adobeSignService.getAgreements(userEmail);
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(50);
		Integer startIndex = size.orElse(0);

		AgreementForm agreementForm = this.adobeSignService.searchAgreements(userEmail, startDate, beforeDate,
				startIndex);

		int totalAgreements = agreementForm.getTotalAgreements().intValue();

		// Page<UserAgreement> agreementPage =
		// adobeSignService.findPaginated(PageRequest.of(currentPage - 1, pageSize),
		// agreementForm.getAgreementIdList());
		PageRequest pageable = PageRequest.of(currentPage - 1, pageSize);
		int startItem = currentPage * pageSize;
		List<UserAgreement> list;

		if (agreementForm.getAgreementIdList().size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, agreementForm.getAgreementIdList().size());
			list = agreementForm.getAgreementIdList().subList(startItem, toIndex);
		}

		Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(list, PageRequest.of(currentPage, pageSize),
				agreementForm.getTotalAgreements());

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

		return "agreementList";
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "hide")
	public String hideAgreements(Model model, @ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.hideAgreements(this.seletedList(agreementForm));
		model.addAttribute("agreementForm", agreementForm);
		return "agreementList";
	}

	private List<UserAgreement> seletedList(AgreementForm agreementForm) {
		List<UserAgreement> seletedList = new ArrayList<>();
		for (UserAgreement agreement : agreementForm.getAgreementIdList()) {
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
		JSONParser parser = new JSONParser();
		org.json.JSONArray jsonArray = null;
		try {
			Object object = parser.parse(data);
			jsonArray = new org.json.JSONArray(object.toString());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		LOGGER.info("data", data);
		final String agreementId = this.adobeSignService.sendAgreement(jsonArray, file1);
		// List<UserAgreement> agreementList = this.adobeSignService.getAgreements();
		return new ResponseEntity<String>(agreementId, HttpStatus.OK);
	}

	/**
	 * Send contract method.
	 *
	 * @return the string
	 */
	@ApiIgnore
	@GetMapping(Constants.LOGIN_PAGE_ENDPOINT)
	public String sendContractMethod() {
		return Constants.LOGIN_HTML;
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
		final String agreementId = this.adobeSignService.sendContract(sendAgreementVO, file1);
		List<UserAgreement> agreementList = this.adobeSignService.getAgreements(null);
		model.addAttribute("agreementList", agreementList);
		return Constants.SEND_FORM_HTML;
	}

	@GetMapping(Constants.SEND_PAGE_ENDPOINT)
	public String sendPageMethod() {
		return Constants.SEND_FORM_HTML;
	}
}