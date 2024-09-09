package com.adobe.acrobatsign.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.model.Agreement;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserWorkflows;
import com.adobe.acrobatsign.model.WorkflowDescription;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.adobe.acrobatsign.util.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowService.class);

	@Value(value = "${baseUrl}")
	private String baseUrl;

	/** The integration key. */
	@Value(value = "${integration-key}")
	private String integrationKey;

	/** The adobe sign service. */
	@Autowired
	RestApiAgreements restApiAgreements;

	@Value(value = "${user_role}")
	private List<String> role;

	/** The integration key. */
	@Value(value = "${agreement_status}")
	private List<String> status;

	/** The integration key. */
	@Value(value = "${agreement_type}")
	private List<String> type;

	@Value(value = "${visibility}")
	private String visibility;

	public List<JSONArray> agreementWithWorkflow(List<String> activeUsers, String startDate, String beforeDate,
			Integer size, String userWorkflowId) {
		String accessToken = null;
		JSONArray agreementList = null;
		List<JSONArray> allUserList = new ArrayList<>();
		JSONObject agreementObj = null;
		final List<String> userIds = new ArrayList<>();
		userIds.addAll(activeUsers);
		try {
			accessToken = Constants.BEARER + this.getIntegrationKey();
			for (int i = 0; i < activeUsers.size(); i++) {
				String currentUser = activeUsers.get(i);
				LOGGER.info("currentUser::" + currentUser);
				agreementObj = this.restApiAgreements.getAgreements(accessToken, currentUser, startDate, beforeDate,
						this.status, size, "");
				agreementObj.put("userEmail", currentUser);
				if (null != agreementObj) {
					agreementList = (JSONArray) ((JSONObject) agreementObj.get(Constants.AGREEMENT_ASSETS_RESULTS))
							.get(Constants.AGREEMENT_ASSETS_RESULT_LIST);
					if (null != agreementObj && null != agreementList && agreementList.size() > 0
							&& null != agreementObj.get(Constants.TOTAL_HITS)
							&& (Long) agreementObj.get(Constants.TOTAL_HITS) > 50) {
						final JSONObject searchPageInfo = (JSONObject) ((JSONObject) agreementObj
								.get(Constants.AGREEMENT_ASSETS_RESULTS)).get(Constants.SEARCH_PAGE_INFO);
						Long nextIndex = (Long) searchPageInfo.get(Constants.NEXT_INDEX);
						while (nextIndex != null) {
							agreementObj = this.restApiAgreements.getAgreements(accessToken, activeUsers.get(i),
									startDate, beforeDate, this.status, nextIndex.intValue(), "");
							final JSONObject searchPageObj = (JSONObject) ((JSONObject) agreementObj
									.get(Constants.AGREEMENT_ASSETS_RESULTS)).get(Constants.SEARCH_PAGE_INFO);
							nextIndex = (Long) searchPageObj.get(Constants.NEXT_INDEX);
							agreementList.addAll(
									(JSONArray) ((JSONObject) agreementObj.get(Constants.AGREEMENT_ASSETS_RESULTS))
											.get(Constants.AGREEMENT_ASSETS_RESULT_LIST));

						}
					}
				}
				for (Object obj : agreementList) {
					// Cast each object to JSONObject
					JSONObject agreement = (JSONObject) obj;

					// Add the key-value pair
					agreement.put("userEmail", currentUser);
				}
				allUserList.addAll(agreementList);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}

		return allUserList;
	}

	public JSONObject getAgreementForUserId(String accessToken, String agrId, String userId) throws IOException {
		// URL to invoke the agreement end point.
		final String url = getBaseURL() + Constants.AGREEMENTS_ENDPOINT + "/" + agrId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		if (null != userId) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "userid:" + userId);
		}

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

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

	public List<String> getRole() {
		return role;
	}

	public List<String> getStatus() {
		return status;
	}

	public List<String> getType() {
		return type;
	}

	public String getVisibility() {
		return visibility;
	}

	public UserWorkflows getWorkflows() {
		String accessToken = null;
		UserWorkflows workflowList = new UserWorkflows();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_WORKFLOWS;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
			JSONObject workflowObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
					RestApiUtils.HttpRequestMethod.GET, headers);
			workflowList = objectMapper.readValue(workflowObj.toJSONString(), UserWorkflows.class);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

		return workflowList;
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

	public void setRole(List<String> role) {
		this.role = role;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public WorkflowDescription workflowDetails(String workflowId) {
		String accessToken = null;
		WorkflowDescription workflowInfo = new WorkflowDescription();
		try {
			accessToken = Constants.BEARER + this.getIntegrationKey();
			final JSONObject workflowDescription = this.restApiAgreements.workflowInfo(accessToken, workflowId);

			// Parse and read response.
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("Workflow Name:: " + workflowDescription.get(Constants.NAME));
			workflowInfo.setName((String) workflowDescription.get(Constants.NAME));
			workflowInfo.setDisplayName((String) workflowDescription.get(Constants.DISPLAY_NAME));
			workflowInfo.setScope((String) workflowDescription.get(Constants.SCOPE));
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return workflowInfo;
	}

	public List<UserAgreement> workflowForAgreements(List<Agreement> userAgreements, String userWorkflowId) {
		List<UserAgreement> userAgreementList = new ArrayList<>();
		List<UserAgreement> completeList = new ArrayList<>();

		for (int j = 0; j < userAgreements.size(); j++) {
			UserAgreement agreement = new UserAgreement();
			String agreementId = userAgreements.get(j).getId();
			String currentUser = userAgreements.get(j).getUserId();

			LOGGER.info("agreementId::" + agreementId);
			JSONObject responseJson = null;
			try {
				String accessToken = Constants.BEARER + this.getIntegrationKey();
				responseJson = getAgreementForUserId(accessToken, agreementId, currentUser);

				if (null != responseJson && null != responseJson.get(Constants.WORKFLOW_ID)
						&& responseJson.get(Constants.WORKFLOW_ID).equals(userWorkflowId)) {
					String workFlowId = (String) responseJson.get(Constants.WORKFLOW_ID);
					agreement.setWorkflowId(workFlowId);
					final JSONObject workflowDescription = workflowInfo(accessToken, workFlowId, currentUser);

					// Parse and read response.
					String workFlowName = (String) workflowDescription.get(Constants.DISPLAY_NAME);
					agreement.setWorkflowName(workFlowName);
					agreement.setId(agreementId);

					agreement.setName(userAgreements.get(j).getName());
					agreement.setStatus(userAgreements.get(j).getStatus());

					agreement.setUserEmail(currentUser);
					userAgreementList.add(agreement);
					completeList.addAll(userAgreementList);
				}
			} catch (IOException e) {

				e.printStackTrace();
				LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getCause());
			}
		}
		return userAgreementList;
	}

	public JSONObject workflowInfo(String accessToken, String workflowId, String userId) throws IOException {
		// URL to invoke the agreement end point.
		final String url = this.getBaseURL() + Constants.GET_WORKFLOWS + "/" + workflowId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		if (null != userId) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "userid:" + userId);
		}
		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}
}
