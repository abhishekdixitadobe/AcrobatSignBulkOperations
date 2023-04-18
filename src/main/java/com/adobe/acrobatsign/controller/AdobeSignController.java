package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "delete")
	public String deleteAgreements(Model model, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.deleteAgreements(seletedList(agreementForm), userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementForm", agreementForm);
		return Constants.LOGIN_HTML;
	}
	private List<UserAgreement> seletedList(AgreementForm agreementForm){
		List<UserAgreement> seletedList = new ArrayList<>();
		for(UserAgreement agreement: agreementForm.getAgreementIdList()) {
			if(agreement.getIsChecked() != null) {
				seletedList.add(agreement);
			}
		}
		return seletedList;
	}
	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "download")
	public ResponseEntity<String> downloadAgreements(HttpServletResponse response, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {
		// List<UserAgreement> agreementList =
		// this.adobeSignService.getAgreements(userEmail);
		// StreamingResponseBody streamResponseBody = out -> {
		
		this.adobeSignService.downloadAgreements(seletedList(agreementForm), userEmail, response);
		// response.setContentLength((int) (zipFile != null ? zipFile.length : 0));
		// };

		// response.setContentType("application/zip");
		// response.setHeader("Content-Disposition", "attachment;
		// filename=agreements.zip");
		// response.addHeader("Pragma", "no-cache");
		// response.addHeader("Expires", "-1");
		// model.addAttribute("userEmail", userEmail);
		// model.addAttribute("agreementList", agreementList);
		// model.addAttribute("agreementForm", agreementForm);
		return ResponseEntity.ok("SUCCESS");
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "formfield")
	public ResponseEntity<StreamingResponseBody> downloadformfields(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm, HttpServletRequest request) {
		StreamingResponseBody streamResponseBody = out -> {
			this.adobeSignService.downloadFormFields(seletedList(agreementForm), userEmail, response);
			// response.setContentLength((int) (zipFile != null ? zipFile.length : 0));
		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=FormFields.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		// model.addAttribute("userEmail", userEmail);
		// model.addAttribute("agreementList", agreementList);
		// model.addAttribute("agreementForm", agreementForm);
		return ResponseEntity.ok(streamResponseBody);
	}

	@PostMapping(Constants.FETCH_AGREEMENT)
	public String fetchUsersAgreement(Model model, @RequestParam(Constants.PARAM_FILE) MultipartFile file1,@RequestParam String startDate,
			@RequestParam String beforeDate) {
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
			allAgreementList.addAll(this.adobeSignService.searchAgreements(userIds.get(i), startDate, beforeDate));
		}
		AgreementForm agreementForm = new AgreementForm();
		agreementForm.setAgreementIdList(allAgreementList);

		model.addAttribute("userEmail", userIds);
		model.addAttribute("agreementList", allAgreementList);
		model.addAttribute("agreementForm", agreementForm);

		return "agreementList";
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

	@PostMapping(Constants.GET_AGREEMENTS)
	public String getUserAgreements(Model model, @RequestParam String userEmail, @RequestParam String startDate,@RequestParam String beforeDate) {
		// List<UserAgreement> agreementList =
		// this.adobeSignService.getAgreements(userEmail);
		List<UserAgreement> agreementList = this.adobeSignService.searchAgreements(userEmail,startDate, beforeDate);

		AgreementForm agreementForm = new AgreementForm();
		agreementForm.setAgreementIdList(agreementList);

		model.addAttribute("userEmail", userEmail);
		model.addAttribute("agreementList", agreementList);
		model.addAttribute("agreementForm", agreementForm);

		return "agreementList";
	}

	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "hide")
	public String hideAgreements(Model model, @ModelAttribute("agreementForm") AgreementForm agreementForm) {
		this.adobeSignService.hideAgreements(seletedList(agreementForm));
		model.addAttribute("agreementForm", agreementForm);
		return "agreementList";
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
		return "agreementList";
	}
}
