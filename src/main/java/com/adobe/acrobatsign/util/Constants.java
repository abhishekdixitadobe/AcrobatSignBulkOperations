package com.adobe.acrobatsign.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * The Class Constants.
 */
public class Constants {

	/**
	 * Represents the various ways that a set of documents can be identified,
	 * depending on the context.
	 */
	public enum DocumentIdentifierName {
		DOCUMENT_URL("documentURL"), LIBRARY_DOCUMENT_ID("libraryDocumentId"),
		TRANSIENT_DOCUMENT_ID("transientDocumentId");

		private final String actualName;

		DocumentIdentifierName(String actualName) {
			this.actualName = actualName;
		}

		@Override
		public String toString() {
			return actualName;
		}
	}

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

	public enum StatusEnum {
		ACTIVE("ACTIVE"),

		INACTIVE("INACTIVE"),

		CREATED("CREATED"),

		UNVERIFIED("UNVERIFIED");

		public static class Adapter extends TypeAdapter<StatusEnum> {
			@Override
			public StatusEnum read(final JsonReader jsonReader) throws IOException {
				final String value = jsonReader.nextString();
				return StatusEnum.fromValue(String.valueOf(value));
			}

			@Override
			public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}
		}

		public static StatusEnum fromValue(String text) {
			for (final StatusEnum b : StatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		private String value;

		StatusEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	/** The Constant ABOUT_US_HTML. */
	public static final String ABOUT_US_HTML = "aboutus";

	/** The Constant ABOUTUS_DETAILS_ENDPOINT. */
	public static final String ABOUTUS_DETAILS_ENDPOINT = "/aboutus-details";

	/** The Constant AGREEMENT_CREATED. */
	public static final String AGREEMENT_CREATED = "Agreement created:agreementId:: ";

	/** The Constant AGREEMENT_SENT_INFO_MSG. */
	public static final String AGREEMENT_SENT_INFO_MSG = "Agreement Sent. Agreement ID =";

	/** The Constant APPLICATION_PDF. */
	public static final String APPLICATION_PDF = "application/pdf";

	public static final String BASE_URL_API_V6 = "/api/rest/v6";

	/** The Constant BEARER. */
	public static final String BEARER = "Bearer ";

	/** The BULK_AGREEMENT_HOME_HTML. */
	public static final String BULK_AGREEMENT_HOME_HTML = "form";

	public static final String BULK_AGREEMENT_HOME_PAGE_ENDPOINT = "/form";

	/** The Constant LOGIN_HTML. */
	public static final String CANCEL_HTML = "CancelAgreementResponse";

	public static final String REMINDER_HTML = "AgreementRemiders";

	public static final String NODATA_HTML = "noData";

	/** The Constant COMBINE_CONTRACT_HTML. */
	public static final String COMBINE_CONTRACT_HTML = "uploadfile";

	/** The Constant COMBINE_ENDPOINT. */
	public static final String COMBINE_ENDPOINT = "/combine";

	/** The Constant COMMON_RETURN_PAGE. */
	public static final String COMMON_RETURN_PAGE = "contract-actions";

	/** The Constant CONTENT_DISPOSITION. */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";

	/** The Constant CONTRACT_FILE_PATH. */
	public static final String CONTRACT_FILE_PATH = "output/contract.pdf";

	/** The Constant CREATE_ENDPOINT. */
	public static final String CREATE_ENDPOINT = "/create";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DELETE_AGREEMENTS = "/manageagreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String CANCEL_AGREEMENTS = "/cancelagreements";

	/** The Constant DESIGNATION. */
	public static final String DESIGNATION = "designation";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DOWNLOAD_AGREEMENTS = "/downloadall";

	/** The Constant DOWNLOAD_CONTRACT_ERROR_MSG. */
	public static final String DOWNLOAD_CONTRACT_ERROR_MSG = "Error in downloading the contract:: ";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String DOWNLOAD_FORM_FIELDS = "/downloadformfields";

	/** The Constant EXIT_FORM_ENDPOINT. */
	public static final String EXIT_FORM_ENDPOINT = "/exitForm";

	/** The Constant EXIT_FORM_HTML. */
	public static final String EXIT_FORM_HTML = "exitForm";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String FETCH_AGREEMENT = "/fetchUsersAgreement";

	public static final String FETCH_AGREEMENT_FOR_IDS = "/agreementListForIds";

	/** The Constant FIRST_NAME. */
	public static final String FIRST_NAME = "firstName";

	public static final String userEmail = "userEmail";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENT_STATUS = "/agreements/{agreementId}";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENTS = "/agreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String GET_AGREEMENTS_WITH_WORKFLOW = "/agreementsWithWorkflow";

	public static final String GET_MULTI_USER_AGREEMENTS = "/multiuseragreements";

	/** The Constant GET_WORKFLOW_DETAILS. */
	public static final String GET_WORKFLOW_DETAILS = "/workflow/{workflowId}";

	public static final String GET_WORKFLOWS = "/workflows";

	public static final String GET_LIBRARY_TEMPLATE = "/libraryTemplate";

	public static final String SEARCH_LIBRARY_TEMPLATE = "/libraryTemplateSearch";

	public static final String RETURN_LIBRARY_TEMPLATE = "libraryTemplateSearch";

	/** The Constant DOWNLOAD_TEMPLATE. */
	public static final String DOWNLOAD_TEMPLATE = "/downloadTemplate";

	public static final String GET_GROUPS = "/groups";

	public static final String GET_EVENTS = "/events";

	/** The Constant HEADER_ATTACHMENT. */
	public static final String HEADER_ATTACHMENT = "attachment; filename=contract.pdf";

	/** The Constant HIRING_DATE. */
	public static final String HIRING_DATE = "hiringDate";

	/** The Constant HR_DEPARTMENT. */
	public static final String HR_DEPARTMENT = "HR Department";

	/** The Constant ID. */
	public static final String ID = "id";

	/** The Constant LOGIN_HTML. */
	public static final String INDEX_HTML = "index";

	/** The Constant LAST_DATE. */
	public static final String LAST_DATE = "lastDate";

	/** The Constant LAST_NAME. */
	public static final String LAST_NAME = "lastName";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);

	/** The Constant LOGIN_PAGE_ENDPOINT. */
	public static final String MAIN_PAGE_ENDPOINT = "/";

	/** The Constant MERGE_ENDPOINT. */
	public static final String MERGE_ENDPOINT = "/merge";

	/** The Constant mimeType. */
	private static final String MIME_TYPE = RestApiUtils.MimeType.PDF.toString();

	/** The Constant NAME. */
	public static final String NAME = "name";

	/** The Constant PARAM_FILE. */
	public static final String PARAM_FILE = "file1";

	/** The Constant PARAM_FILES. */
	public static final String PARAM_FILES = "files";

	/** The Constant PDF_ENDPOINT. */
	public static final String PDF_ENDPOINT = "/pdf";

	/** The Constant PERSON_FULL_NAME. */
	public static final String PERSON_FULL_NAME = "personFullName";

	/** The Constant REARRANGE_CONTRACT_HTML. */
	public static final String REARRANGE_CONTRACT_HTML = "rearrange";

	/** The Constant REARRANGE_FILE_ENDPOINT. */
	public static final String REARRANGE_FILE_ENDPOINT = "/rearrangeFile";

	/** The Constant REARRANGE_PAGE_ENDPOINT. */
	public static final String REARRANGE_PAGE_ENDPOINT = "/rearrangePage";

	/** The Constant RESIGNATION_DATE. */
	public static final String RESIGNATION_DATE = "resignationDate";

	/** The Constant RESIGNATION_REASON. */
	public static final String RESIGNATION_REASON = "resignationReason";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String SEND_AGREEMENT = "/sendagreement";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String SEND_FOR_SIGNATURE_ENDPOINT = "/sendsignature";

	/** The Constant GET_HELP. */
	public static final String GET_HELP = "/helpx";

	/** The Constant SEND_FORM_HTML. */
	public static final String SEND_FORM_HTML = "sendForm";
	/** The Constant SEND_FORM_HTML. */

	public static final String GET_HELPX = "helpx";

	/** The Constant SEND_PAGE_ENDPOINT. */
	public static final String SEND_PAGE_ENDPOINT = "/send";

	/** The Constant TRANSIENT_DOCUMENT_ID. */
	public static final String TRANSIENT_DOCUMENT_ID = "transientDocumentId";

	/** The Constant WELCOME_ENDPOINT. */
	public static final String WELCOME_ENDPOINT = "/welcome";

	/** The Constant WELCOME_HTML_PAGE. */
	public static final String WELCOME_HTML_PAGE = "welcome";

	public static final String GET_ALL_USERS = "/users";

	public static final String GET_WIDGET = "/widgets";

	public static final String GET_USERS_WIDGET = "/userWidgets";

	public static final String GET_AGREEMENTS_FOR_WIDGET = "/widgets/{widgetId}/{email}";

	public static final String WORKFLOW_ID = "workflowId";

	public static final String DISPLAY_NAME = "displayName";

	public static final String STATUS = "status";

	public static final String MODIFIED_DATE = "modifiedDate";

	public static final String AGREEMENT_ASSETS_RESULTS = "agreementAssetsResults";

	public static final String AGREEMENT_ASSETS_RESULT_LIST = "agreementAssetsResultList";

	public static final String TOTAL_HITS = "totalHits";

	public static final String SEARCH_PAGE_INFO = "searchPageInfo";

	public static final String NEXT_INDEX = "nextIndex";

	public static final String SCOPE = "scope";

	public static final String WORKFLOW_LIST = "workflowList";

	public static final String GROUP_LIST = "userGroupsList";

	public static final String AGREEMENT_INFO = "agreementInfo";

	public static final String PAGE_NUMBERS = "pageNumbers";

	public static final String USER_WORKFLOW = "userWorkflow";

	public static final String START_DATE = "startDate";

	public static final String BEFORE_DATE = "beforeDate";

	public static final String AGREEMENT_PAGE = "agreementPage";

	public static final String AGREEMENT_LIST = "agreementList";

	public static final String TOTAL_AGREEMENTS = "totalAgreements";

	public static final String AGREEMENT_FORM = "agreementForm";

	public static final String ACTIVE_USER_LIST = "activeUserList";

	public static final String AGREEMENTS = "agreements";

	public static final String USER_EMAIL = "userEmail";

	public static final String DOWNLOAD_WEBFORMS = "/downloadWebforms";

	public static final String AGREEMENTS_FOR_WEBFORM_LIST = "agreementsForWebformList";

	public static final String WIDGET_LIST = "widgetList";

	public static final String AGREEMENT_ID_LIST = "agreementIdList";

	public static final String USER_IDS = "userIds";

	public static final String TOTAL_TEMPLATES = "totalTemplates";

	public static final String NEXT_INDEX_MAP = "nextIndexMap";

	public static final String LIBRARY_TEMPLATE_LIST = "libraryTemplateList";

	public static final String LIBRARY_TEMPLATES = "libraryTemplates";

	public static final String DOWNLOAD_TEMPLATE_PARAM = "downloadTemplate";

	public static final String FORM_FIELD_TEMPLATE = "formfieldTemplate";

	public static final String HIDE_TEMPLATE = "hideTemplate";

	public static final String WORKFLOWS_FOR_AGREEMENTS = "/workflowsForAgreements";

	public static final String DOWNLOAD_WORKFLOW_AGREEMENTS = "/downloadworkflows";

	public static final String AGREEMENTS_ENDPOINT = "/agreements";

	/** The Constant SEND_FOR_SIGNATURE_ENDPOINT. */
	public static final String FETCH_TEMPLATE = "/fetchUsersTemplate";

	public static final String FORBIDDEN_ERROR = "The API caller does not have the permission to execute this operation";

	public static final String ERROR = "error";
	public static final String REGEX_PATTERN = "^[^<>:\\\"/\\\\\\\\|?*]*$";

	private Constants() {
		LOGGER.info("Private Constructor");
	}
}
