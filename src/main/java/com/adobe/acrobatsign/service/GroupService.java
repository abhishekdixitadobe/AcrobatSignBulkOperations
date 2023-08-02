package com.adobe.acrobatsign.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserGroups;
import com.adobe.acrobatsign.model.UserWorkflows;
import com.adobe.acrobatsign.model.WorkflowDescription;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.adobe.acrobatsign.util.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GroupService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

	@Value(value = "${baseUrl}")
	private String baseUrl;

	/** The integration key. */
	@Value(value = "${integration-key}")
	private String integrationKey;

	/** The adobe sign service. */
	@Autowired
	RestApiAgreements restApiAgreements;


	public String getBaseUrl() {
		return baseUrl;
	}

	private String getBaseURL() {
		return baseUrl + Constants.BASE_URL_API_V6;
	}

	public String getIntegrationKey() {
		return integrationKey;
	}

	public RestApiAgreements getRestApiAgreements() {
		return restApiAgreements;
	}
	
	public UserGroups getGroups() {
		String accessToken = null;
		
		UserGroups usrGroupList = new UserGroups();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_GROUPS;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JSONObject groupObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
					RestApiUtils.HttpRequestMethod.GET, headers);
			usrGroupList = objectMapper.readValue(groupObj.toJSONString(), UserGroups.class);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

		return usrGroupList;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
	}

	public void setRestApiAgreements(RestApiAgreements restApiAgreements) {
		this.restApiAgreements = restApiAgreements;
	}
}
