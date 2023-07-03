package com.adobe.acrobatsign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.adobe.acrobatsign.util.Constants;

import springfox.documentation.annotations.ApiIgnore;

@Controller
public class ResourceFileController {

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

	/**
	 * open bulk agreement page.
	 *
	 * @return the string
	 */
	@ApiIgnore
	@GetMapping(Constants.BULK_AGREEMENT_HOME_PAGE_ENDPOINT)
	public String openBulkAgreementHome() {
		return Constants.BULK_AGREEMENT_HOME_HTML;
	}

	/**
	 * open main page.
	 *
	 * @return the string
	 */
	@ApiIgnore
	@GetMapping(Constants.MAIN_PAGE_ENDPOINT)
	public String sendContractMethod() {
		return Constants.INDEX_HTML;
	}

	@GetMapping(Constants.SEND_PAGE_ENDPOINT)
	public String sendPageMethod() {
		return Constants.SEND_FORM_HTML;
	}
}
