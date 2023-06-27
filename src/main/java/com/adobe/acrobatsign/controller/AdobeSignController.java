package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

import com.adobe.acrobatsign.model.AccessTokenVO;
import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.RefreshTokenVO;
import com.adobe.acrobatsign.model.SendAgreementVO;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.AdobeSignService;
import com.adobe.acrobatsign.util.Constants;

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
	
	private String paramCode = null;
	private String refreshTokenCode = null;

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "cancel")
	public String cancelReminders(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.cancelReminders(this.seletedList(agreementForm), userEmail, refreshTokenCode);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.LOGIN_HTML;
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "delete")
	public String deleteAgreements(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.deleteAgreements(this.seletedList(agreementForm), userEmail, refreshTokenCode);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.LOGIN_HTML;
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "download")
	public ResponseEntity<StreamingResponseBody> downloadAgreements(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm, @ModelAttribute("refreshToken") String refreshToken) {
		System.out.println("Refresh Token while Downloading "+refreshTokenCode);
		StreamingResponseBody streamResponseBody = out -> {
			this.adobeSignService.downloadAgreements(this.seletedList(agreementForm), userEmail, response, refreshTokenCode);
		};
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=agreements.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return ResponseEntity.ok(streamResponseBody);
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "formfield")
	public ResponseEntity<StreamingResponseBody> downloadformfields(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm,
			HttpServletRequest request, @ModelAttribute("refreshToken") String refreshToken) {
		StreamingResponseBody streamResponseBody = out -> {
			this.adobeSignService.downloadFormFields(this.seletedList(agreementForm), userEmail, response,  refreshTokenCode);
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

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		List<String> userIds = new ArrayList<>();
		List<UserAgreement> allAgreementList = new ArrayList<>();

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
		for (int i = 1; i < userIds.size(); i++) {
			// allAgreementList.addAll(this.adobeSignService.searchAgreements(userIds.get(i),
			// startDate, beforeDate));
		}
		Page<UserAgreement> agreementPage = this.adobeSignService
				.findPaginated(PageRequest.of(currentPage - 1, pageSize), allAgreementList);

		AgreementForm agreementForm = new AgreementForm();
		agreementForm.setAgreementIdList(allAgreementList);

		int totalPages = agreementPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("userEmail", userIds);
		model.addAttribute("agreementList", allAgreementList);
		model.addAttribute("agreementPage", agreementPage);
		model.addAttribute("agreementForm", agreementForm);

		return "agreementList";
	}

	@GetMapping(Constants.GET_AGREEMENT_STATUS)
	public String getAgreementIdStatus(Model model, @PathVariable String agreementId) {
		LOGGER.info(Constants.AGREEMENT_CREATED, agreementId);
		AgreementInfo agreementInfo = this.adobeSignService.getContractStatus(agreementId, refreshTokenCode);
		List<AgreementInfo> agreementInfoList = new ArrayList<>();
		agreementInfoList.add(agreementInfo);
		model.addAttribute("agreementInfo", agreementInfo);
		model.addAttribute("senderEmail", agreementInfo.getSenderEmail());
		model.addAttribute("name", agreementInfo.getName());
		model.addAttribute("status", agreementInfo.getStatus());
		model.addAttribute("partcipantSet", agreementInfo.getParticipantSet());
		return "agreementdetails";
	}
	/*
	 * @GetMapping(Constants.GET_AGREEMENTS) public String getAgreements(Model
	 * model) { List<UserAgreement> agreementList =
	 * this.adobeSignService.getAgreements(); model.addAttribute("agreementList",
	 * agreementList); return "agreementList"; }
	 */

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

	@GetMapping(Constants.GET_AGREEMENTS)
	public String getPaginatedUserAgreements(Model model, @RequestParam String userEmail,
			@RequestParam String startDate, @RequestParam String beforeDate,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		// List<UserAgreement> agreementList =
		// this.adobeSignService.getAgreements(userEmail);
		int currentPage = page.orElse(1);
		int pageSize = 50;

		AgreementForm agreementForm = this.adobeSignService.searchAgreements(userEmail, startDate, beforeDate,
				size.get(), refreshTokenCode);

		int totalAgreements = agreementForm.getTotalAgreements().intValue();

		// Page<UserAgreement> agreementPage =
		// adobeSignService.findPaginated(PageRequest.of(currentPage - 1, pageSize),
		// agreementForm.getAgreementIdList());
		PageRequest pageable = PageRequest.of(currentPage - 1, pageSize);
		int startItem = currentPage * pageSize;
		List<UserAgreement> list;

		/*
		 * if (totalAgreements < startItem) { list = Collections.emptyList(); } else {
		 * int toIndex = Math.min(startItem + pageSize, totalAgreements); list =
		 * agreementForm.getAgreementIdList().subList(startItem, toIndex); }
		 */

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

	public String getToken() {
		System.out.println("===inside Generate Token=======" +paramCode);
		AccessTokenVO accessTokenVO = new AccessTokenVO();
		//Hard coding-will pick up from properties file
		/*
		 * accessTokenVO.setGrant_type("authorization_code");
		 * accessTokenVO.setClient_id("CBJCHBCAABAAdGwNUmexrdzHFcMKjsRNJIdPu9489GA6");
		 * accessTokenVO.setClient_secret("HeJs-zyxjxRGKzDCS4hQshvij5wxofIs");
		 * accessTokenVO.setRedirect_uri("https://localhost:443");
		 */
		accessTokenVO.setCode(paramCode);
		return this.adobeSignService.callApi(accessTokenVO);
	}
	
	public String getRefreshToken(String paramCode) {
		RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
		/*
		 * refreshTokenVO.setGrant_type("refresh_token");
		 * refreshTokenVO.setClient_id("CBJCHBCAABAAdGwNUmexrdzHFcMKjsRNJIdPu9489GA6");
		 * refreshTokenVO.setClient_secret("HeJs-zyxjxRGKzDCS4hQshvij5wxofIs");
		 */
		refreshTokenVO.setRefresh_token(getToken());
		return this.adobeSignService.callRefreshApi(refreshTokenVO);
	}

	@GetMapping(Constants.GENERATE_URL)
	public String getURL(Model model, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("===inside get URL===");
		model.addAttribute("url", this.adobeSignService.getURL());
		return Constants.WELCOME_HTML_PAGE;
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
				startIndex, refreshTokenCode);

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
		this.adobeSignService.hideAgreements(this.seletedList(agreementForm), refreshTokenCode);
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
		final String agreementId = this.adobeSignService.sendAgreement(jsonArray, file1, refreshTokenCode);
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
	public String sendContractMethod(Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("refreshToken") String refreshToken) {

		model.addAttribute("url", this.adobeSignService.getURL());
		model.addAttribute("code", request.getParameter("code"));
		paramCode =request.getParameter("code");
		if(null!=paramCode) {
			 refreshToken=getRefreshToken(paramCode);
			 model.addAttribute("refreshToken", refreshToken);
			 refreshTokenCode = refreshToken;
			System.out.println("Refresh Token Controller: "+refreshToken);
		}
		if(request.getQueryString()!=null && request.getQueryString()!="") {
			return Constants.LOGIN_HTML;
		}
		return Constants.LOGIN_FINAL_HTML;
	}
	
	@PostMapping(Constants.LOGIN_PAGE_ENDPOINT)
	public String getContractMethod(Model model) {
		model.addAttribute("url", this.adobeSignService.getURL());
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
		final String agreementId = this.adobeSignService.sendContract(sendAgreementVO, file1, refreshTokenCode);
		List<UserAgreement> agreementList = this.adobeSignService.getAgreements(null, refreshTokenCode);
		model.addAttribute("agreementList", agreementList);
		return Constants.SEND_FORM_HTML;
	}

	@GetMapping(Constants.SEND_PAGE_ENDPOINT)
	public String sendPageMethod() {
		return Constants.SEND_FORM_HTML;
	}
}
