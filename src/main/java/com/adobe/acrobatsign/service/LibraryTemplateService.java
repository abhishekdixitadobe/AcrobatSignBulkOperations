package com.adobe.acrobatsign.service;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.model.LibraryDocument;
import com.adobe.acrobatsign.model.LibraryDocuments;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LibraryTemplateService {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryTemplateService.class);

	/** The adobe sign service. */
	@Autowired
	RestApiAgreements restApiAgreements;

	@Autowired
	UserService userService;

	/** The integration key. */
	@Value(value = "${integration-key}")
	private String integrationKey;

	boolean containsElement(List<LibraryDocument> list, String templateId) {
		return list.stream().anyMatch(p -> p.getId().equals(templateId));
	}

	public ZipOutputStream downloadTemplateFormFields(List<LibraryDocument> libraryTemplateList, String userEmail,
			HttpServletResponse response) {
		String accessToken = null;
		ZipOutputStream zos = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			zos = restApiAgreements.downloadTemplateFormFields(accessToken, libraryTemplateList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return zos;
	}

	public String downloadTemplates(List<LibraryDocument> seletedTemplateList, String userEmail,
			HttpServletResponse response) {
		String accessToken = null;
		String combinedTemplate = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			combinedTemplate = restApiAgreements.downloadTemplates(accessToken, seletedTemplateList, userEmail,
					response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return combinedTemplate;

	}

	public MultiUserAgreementDetails fetchLibraryTemplate(List<String> userIds) {
		String accessToken = null;
		JSONObject libraryDocuments = null;
		List<JSONObject> allTemplateList = new ArrayList<>();
		List<LibraryDocument> listLibraryDocument = new ArrayList<>();
		MultiUserAgreementDetails multiUserAgreementDetails = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			multiUserAgreementDetails = new MultiUserAgreementDetails();
			List<String> userEmailList = new ArrayList<>();
			if (null != userIds) {
				for (int i = 1; i < userIds.size(); i++) {
					LibraryDocuments documentPerUser = new LibraryDocuments();
					String txtUserEmail = userIds.get(i);
					userEmailList.add(txtUserEmail);
					libraryDocuments = restApiAgreements.getUserTemplate(accessToken, txtUserEmail, "");
					allTemplateList.add(libraryDocuments);
					if (null != (JSONObject) libraryDocuments.get("page")
							&& !((JSONObject) libraryDocuments.get("page")).isEmpty()
							&& ((JSONObject) libraryDocuments.get("page")).containsKey("nextCursor")
							&& null != ((JSONObject) libraryDocuments.get("page")).get("nextCursor")) {
						String cursor = (String) ((JSONObject) libraryDocuments.get("page")).get("nextCursor");
						while (null != (JSONObject) libraryDocuments.get("page")
								&& !((JSONObject) libraryDocuments.get("page")).isEmpty()) {
							libraryDocuments = restApiAgreements.getUserTemplate(accessToken, txtUserEmail, cursor);
							if (null != (JSONObject) libraryDocuments.get("page")
									&& !((JSONObject) libraryDocuments.get("page")).isEmpty()
									&& ((JSONObject) libraryDocuments.get("page")).containsKey("nextCursor")
									&& null != ((JSONObject) libraryDocuments.get("page")).get("nextCursor")) {
								cursor = (String) ((JSONObject) libraryDocuments.get("page")).get("nextCursor");
							}
							allTemplateList.add(libraryDocuments);

						}
					}
					for (int j = 0; j < allTemplateList.size(); j++) {
						ObjectMapper objectMapper = new ObjectMapper();
						documentPerUser = objectMapper.readValue(allTemplateList.get(j).toJSONString(),
								LibraryDocuments.class);
						// owner condition
						for (int k = 0; k < documentPerUser.getLibraryDocumentList().size(); k++) {
							if (txtUserEmail.equalsIgnoreCase(
									documentPerUser.getLibraryDocumentList().get(k).getOwnerEmail())) {
								listLibraryDocument.add(documentPerUser.getLibraryDocumentList().get(k));
							}
						}
					}

				}
			}
			multiUserAgreementDetails.setLibraryDocumentList(listLibraryDocument);
			multiUserAgreementDetails.setTotalTemplates((long) listLibraryDocument.size());
			multiUserAgreementDetails.setUserEmails(userEmailList);

		} catch (Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}
		return multiUserAgreementDetails;
	}

	public String getIntegrationKey() {
		return integrationKey;
	}

	public void hideTemplates(List<LibraryDocument> seletedList) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.hideTemplates(accessToken, seletedList);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
	}
}
