package com.adobe.acrobatsign.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.LibraryDocument;
import com.adobe.acrobatsign.model.LibraryDocuments;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.LibraryTemplateService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class LibraryTemplateController {
	
	/** The adobe sign service. */
	@Autowired
	LibraryTemplateService libraryTemplateService;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryTemplateController.class);

	@Value("${pageSize}")
	public String maxLimit;
	
	@RequestMapping(value = Constants.GET_LIBRARY_TEMPLATE, method = RequestMethod.GET)
	public String getLibraryTemplate(Model model) {
		AgreementForm agreementForm = new AgreementForm();
		MultiUserAgreementDetails multiUserAgreementDetails = libraryTemplateService.fetchLibraryTemplate();
		
		//New code start
		final long totalTemplates = multiUserAgreementDetails.getTotalTemplates();
		agreementForm.setAgreementIdList(multiUserAgreementDetails.getAgreementList());
		agreementForm.setLibraryTemplateList(multiUserAgreementDetails.getLibraryDocumentList());
		agreementForm.setTotalAgreements(multiUserAgreementDetails.getTotalAgreements());
		multiUserAgreementDetails.setNextIndexMap(multiUserAgreementDetails.getNextIndexMap());

		/*final Page<LibraryDocument> templatePage = new PageImpl<LibraryDocument>(
				multiUserAgreementDetails.getLibraryDocumentList(),
				PageRequest.of(page.get() - 1, Integer.parseInt(maxLimit)), totalTemplates);
		final long totalPages = templatePage.getTotalPages(); 
		if (totalPages > 0) {
			final List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) totalPages).boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}*/

		model.addAttribute("userIds", multiUserAgreementDetails.getUserEmails());
		if (multiUserAgreementDetails.getUserEmails().size() > 1) {
			model.addAttribute("userEmail", multiUserAgreementDetails.getUserEmails().get(1));
		} else {
			model.addAttribute("userEmail", null);
		}

		//model.addAttribute("templatePage", templatePage);
		//model.addAttribute("templateList", multiUserAgreementDetails.getLibraryDocumentList());
		model.addAttribute("totalTemplates", multiUserAgreementDetails.getTotalTemplates());
		model.addAttribute("agreementForm", agreementForm);
		model.addAttribute("nextIndexMap", multiUserAgreementDetails.getNextIndexMap());
		//New code end
		model.addAttribute("libraryTemplateList", multiUserAgreementDetails.getLibraryDocumentList());
		return "libraryTemplates";
	}
	
	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "downloadTemplate")
	public ResponseEntity<StreamingResponseBody> downloadTemplates(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm) {
		final StreamingResponseBody streamResponseBody = out -> {
			libraryTemplateService.downloadTemplates(seletedList(agreementForm), userEmail, response);
		};
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=agreements.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return new ResponseEntity(streamResponseBody, HttpStatus.OK);
	}
	
	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "formfieldTemplate")
	public ResponseEntity<StreamingResponseBody> downloadformfields(HttpServletResponse response,
			@RequestParam String userEmail, @ModelAttribute("agreementForm") AgreementForm agreementForm,
			HttpServletRequest request) {
		final StreamingResponseBody streamResponseBody = out -> {
			libraryTemplateService.downloadTemplateFormFields(seletedList(agreementForm), userEmail, response);
		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=FormFields.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return ResponseEntity.ok(streamResponseBody);
	}
	
	private List<LibraryDocument> seletedList(AgreementForm agreementForm) {
		final List<LibraryDocument> seletedList = new ArrayList<>();
		for (final LibraryDocument document : agreementForm.getLibraryTemplateList()) {
			if (document.getIsChecked() != null) {
				seletedList.add(document);
			}
		}
		return seletedList;
	}
	
	@RequestMapping(value = Constants.DELETE_AGREEMENTS, method = RequestMethod.POST, params = "hideTemplate")
	public String hideTemplates(Model model, @ModelAttribute("agreementForm") AgreementForm agreementForm){
		libraryTemplateService.hideTemplates(seletedList(agreementForm));
		model.addAttribute("agreementForm", agreementForm);
		return getLibraryTemplate(model);
	}

}
