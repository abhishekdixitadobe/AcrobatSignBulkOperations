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

import com.adobe.acrobatsign.model.DetailedUserInfo;
import com.adobe.acrobatsign.model.LibraryDocument;
import com.adobe.acrobatsign.model.LibraryDocuments;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.model.UserAgreement;
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
	

	public String getIntegrationKey() {
		return integrationKey;
	}


	public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
	}

	public MultiUserAgreementDetails fetchLibraryTemplate() {
		String accessToken = null;
		JSONObject libraryDocuments = null;
		List<LibraryDocuments> listLibraryDocuments = null;
		List<LibraryDocument> listLibraryDocument =null;
		MultiUserAgreementDetails multiUserAgreementDetails = null;
		try{
			List<DetailedUserInfo> userlist =userService.activeUsers();
			accessToken = Constants.BEARER + getIntegrationKey();
			listLibraryDocuments = new ArrayList<LibraryDocuments>();
			multiUserAgreementDetails = new MultiUserAgreementDetails();
			List<String> userEmailList = new ArrayList<>();
			if (null!=userlist) {
				for(int i=0;i<userlist.size();i++) {
					LibraryDocuments documentPerUser = new LibraryDocuments();
					String txtUserEmail  = userlist.get(i).getEmail();
					userEmailList.add(txtUserEmail);
					libraryDocuments = restApiAgreements.getUserTemplate(accessToken, txtUserEmail);
					ObjectMapper objectMapper = new ObjectMapper();
					documentPerUser = objectMapper.readValue(libraryDocuments.toJSONString(), LibraryDocuments.class);
					//if(txtUserEmail.equalsIgnoreCase(documentPerUser.getLibraryDocumentList().get(i).getOwnerEmail())) {
						listLibraryDocuments.add(documentPerUser);
					//}
				}
				listLibraryDocument=getLibraryTemplate(listLibraryDocuments);
			}
			multiUserAgreementDetails.setLibraryDocumentList(listLibraryDocument);
			multiUserAgreementDetails.setTotalTemplates((long) listLibraryDocument.size());
			multiUserAgreementDetails.setUserEmails(userEmailList);
			
		} catch(Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}
		return multiUserAgreementDetails;
	}


	private List<LibraryDocument> getLibraryTemplate(List<LibraryDocuments> listLibraryDocuments) {
			List<LibraryDocument> libraryDocument = new ArrayList<LibraryDocument>();
			for (int i=0; i<listLibraryDocuments.size();i++) {
				for(int j=0;j<listLibraryDocuments.get(i).getLibraryDocumentList().size();j++) {
					
					if(!containsElement(libraryDocument, listLibraryDocuments.get(i).getLibraryDocumentList().get(j).getId())) {
						libraryDocument.add(listLibraryDocuments.get(i).getLibraryDocumentList().get(j));
					}
				}
		    }
		    return libraryDocument;
		
	}
	
	boolean containsElement(List<LibraryDocument> list, String templateId) {
	    return list.stream().anyMatch(p -> p.getId().equals(templateId));
	}


	public String downloadTemplates(List<LibraryDocument> seletedTemplateList, String userEmail, HttpServletResponse response) {
		String accessToken = null;
		String combinedTemplate = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			combinedTemplate = restApiAgreements.downloadTemplates(accessToken, seletedTemplateList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return combinedTemplate;
		
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


	public void hideTemplates(List<LibraryDocument> seletedList) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.hideTemplates(accessToken, seletedList);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		
	}
}
