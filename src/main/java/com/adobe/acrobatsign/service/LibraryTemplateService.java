package com.adobe.acrobatsign.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.model.LibraryDocument;
import com.adobe.acrobatsign.model.LibraryDocuments;
import com.adobe.acrobatsign.model.PageInfo;
import com.adobe.acrobatsign.model.UserInfo;
import com.adobe.acrobatsign.model.UsersInfo;
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

	public List<LibraryDocuments> fetchLibraryTemplate() {
		String accessToken = null;
		JSONObject libraryDocuments = null;
		List<LibraryDocuments> listLibraryDocuments = null;
		try{
			UsersInfo usersInfo=userService.fetchUSer();
			accessToken = Constants.BEARER + getIntegrationKey();
			listLibraryDocuments = new ArrayList<LibraryDocuments>();
			if (null!=usersInfo.getUserInfoList()) {
				for(int i=0;i<usersInfo.getUserInfoList().size();i++) {
					List<LibraryDocument> libraryDocumentList = new ArrayList<LibraryDocument>();
					LibraryDocuments documentPerUser = new LibraryDocuments();
					String txtUserEmail  = usersInfo.getUserInfoList().get(i).getEmail();
					libraryDocuments = restApiAgreements.getUserTemplate(accessToken, txtUserEmail);
					for(int j=0;j<libraryDocuments.size();j++) {
						LibraryDocument document = new LibraryDocument();
						document.setId(null);
						document.setModifiedDate(((JSONObject) libraryDocuments.get(i)).get("modifiedDate").toString());
						document.setName(((JSONObject) libraryDocuments.get(i)).get("name").toString());
						document.setOwnerEmail(((JSONObject) libraryDocuments.get(i)).get("ownerEmail").toString());
						document.setStatus(((JSONObject) libraryDocuments.get(i)).get("status").toString());
						
						libraryDocumentList.add(document);
					}
					documentPerUser.setLibraryDocument(libraryDocumentList);
					documentPerUser.setPageInfo(null);
					
					listLibraryDocuments.add(documentPerUser);
				}
			}
		} catch(Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}
		return listLibraryDocuments;
	}
}
