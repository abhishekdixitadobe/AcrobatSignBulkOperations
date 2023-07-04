package com.adobe.acrobatsign.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Constants.
 */
public class Constants {

	/**
	 * Representation of common HTTP header fields relevant for the REST API.
	 */
	public enum signRole {

		/** The approver. */
		APPROVER("APPROVER"),
		/** The signer. */
		SIGNER("SIGNER");

		/** The role name. */
		private final String roleName;

		/**
		 * Instantiates a new sign role.
		 *
		 * @param roleName the role name
		 */
		signRole(String roleName) {
			this.roleName = roleName;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return roleName;
		}
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);

	/** The Constant WELCOME_ENDPOINT. */
	public static final String WELCOME_ENDPOINT = "/welcome";

	/** The Constant COMBINE_ENDPOINT. */
	public static final String COMBINE_ENDPOINT = "/combine";

	/** The Constant MERGE_ENDPOINT. */
	public static final String MERGE_ENDPOINT = "/merge";

	/** The Constant CREATE_ENDPOINT. */
	public static final String CREATE_ENDPOINT = "/create";

	/** The Constant PDF_ENDPOINT. */
	public static final String PDF_ENDPOINT = "/pdf";

	/** The Constant EXIT_FORM_ENDPOINT. */
	public static final String EXIT_FORM_ENDPOINT = "/exitForm";

	/** The Constant REARRANGE_PAGE_ENDPOINT. */
	public static final String REARRANGE_PAGE_ENDPOINT = "/rearrangePage";

	/** The Constant REARRANGE_FILE_ENDPOINT. */
	public static final String REARRANGE_FILE_ENDPOINT = "/rearrangeFile";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String SEND_FOR_SIGNATURE_ENDPOINT = "/sendsignature";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String SEND_AGREEMENT = "/sendagreement";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String FETCH_AGREEMENT = "/fetchUsersAgreement";

	public static final String FETCH_AGREEMENT_FOR_IDS = "/agreementListForIds";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENTS = "/agreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENTS_WITH_WORKFLOW = "/agreementsWithWorkflow";

	public static final String GET_MULTI_USER_AGREEMENTS = "/multiuseragreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DOWNLOAD_AGREEMENTS = "/downloadall";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DELETE_AGREEMENTS = "/deleteagreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DOWNLOAD_FORM_FIELDS = "/downloadformfields";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENT_STATUS = "/agreements/{agreementId}";

	/** The Constant GET_WORKFLOW_DETAILS. */
	public static final String GET_WORKFLOW_DETAILS = "/workflow/{workflowId}";

	/** The Constant SEND_PAGE_ENDPOINT. */
	public static final String SEND_PAGE_ENDPOINT = "/send";

	/** The Constant ABOUTUS_DETAILS_ENDPOINT. */
	public static final String ABOUTUS_DETAILS_ENDPOINT = "/aboutus-details";

	/** The Constant LOGIN_PAGE_ENDPOINT. */
	public static final String MAIN_PAGE_ENDPOINT = "/";

	public static final String BULK_AGREEMENT_HOME_PAGE_ENDPOINT = "/form";

	/** The Constant PARAM_FILE. */
	public static final String PARAM_FILE = "file1";

	/** The Constant PARAM_FILES. */
	public static final String PARAM_FILES = "files";

	/** The Constant DOWNLOAD_CONTRACT_ERROR_MSG. */
	public static final String DOWNLOAD_CONTRACT_ERROR_MSG = "Error in downloading the contract:: ";

	/** The Constant COMBINE_CONTRACT_HTML. */
	public static final String COMBINE_CONTRACT_HTML = "uploadfile";

	/** The Constant REARRANGE_CONTRACT_HTML. */
	public static final String REARRANGE_CONTRACT_HTML = "rearrange";

	/** The Constant COMMON_RETURN_PAGE. */
	public static final String COMMON_RETURN_PAGE = "contract-actions";

	/** The Constant EXIT_FORM_HTML. */
	public static final String EXIT_FORM_HTML = "exitForm";

	/** The Constant WELCOME_HTML_PAGE. */
	public static final String WELCOME_HTML_PAGE = "welcome";

	/** The Constant NAME. */
	public static final String NAME = "Name";

	/** The Constant FIRST_NAME. */
	public static final String FIRST_NAME = "firstName";

	/** The Constant LAST_NAME. */
	public static final String LAST_NAME = "lastName";

	/** The Constant PERSON_FULL_NAME. */
	public static final String PERSON_FULL_NAME = "personFullName";

	/** The Constant DESIGNATION. */
	public static final String DESIGNATION = "designation";

	/** The Constant RESIGNATION_DATE. */
	public static final String RESIGNATION_DATE = "resignationDate";

	/** The Constant LAST_DATE. */
	public static final String LAST_DATE = "lastDate";

	/** The Constant HIRING_DATE. */
	public static final String HIRING_DATE = "hiringDate";

	/** The Constant RESIGNATION_REASON. */
	public static final String RESIGNATION_REASON = "resignationReason";

	/** The Constant LOGIN_HTML. */
	public static final String INDEX_HTML = "index";

	/** The BULK_AGREEMENT_HOME_HTML. */
	public static final String BULK_AGREEMENT_HOME_HTML = "form";

	/** The Constant LOGIN_HTML. */
	public static final String CANCEL_HTML = "CancelAgreementResponse";

	/** The Constant APPLICATION_PDF. */
	public static final String APPLICATION_PDF = "application/pdf";

	/** The Constant HEADER_ATTACHMENT. */
	public static final String HEADER_ATTACHMENT = "attachment; filename=contract.pdf";

	/** The Constant CONTENT_DISPOSITION. */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";

	/** The Constant CONTRACT_FILE_PATH. */
	public static final String CONTRACT_FILE_PATH = "output/contract.pdf";

	/** The Constant AGREEMENT_CREATED. */
	public static final String AGREEMENT_CREATED = "Agreement created:agreementId:: ";

	/** The Constant SEND_FORM_HTML. */
	public static final String SEND_FORM_HTML = "sendForm";

	/** The Constant ABOUT_US_HTML. */
	public static final String ABOUT_US_HTML = "aboutus";

	/** The Constant BEARER. */
	public static final String BEARER = "Bearer ";

	/** The Constant TRANSIENT_DOCUMENT_ID. */
	public static final String TRANSIENT_DOCUMENT_ID = "transientDocumentId";

	/** The Constant HR_DEPARTMENT. */
	public static final String HR_DEPARTMENT = "HR Department";

	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant AGREEMENT_SENT_INFO_MSG. */
	public static final String AGREEMENT_SENT_INFO_MSG = "Agreement Sent. Agreement ID =";

	private Constants() {
		LOGGER.info("Private Constructor");
	}
}
