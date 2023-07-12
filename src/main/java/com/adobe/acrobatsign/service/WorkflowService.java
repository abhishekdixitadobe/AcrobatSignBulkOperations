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

import com.adobe.acrobatsign.model.AgreementForm;
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

	public AgreementForm agreementWithWorkflow(String userEmail, String startDate, String beforeDate, Integer size,
			String userWorkflowId) {
		String accessToken = null;
		JSONArray agreementList = null;
		JSONObject agreementObj = null;
		AgreementForm agreementForm = new AgreementForm();
		try {
			accessToken = Constants.BEARER + this.getIntegrationKey();
			agreementObj = this.restApiAgreements.getAgreements(accessToken, userEmail, startDate, beforeDate,
					this.status, size);
			agreementList = (JSONArray) ((JSONObject) agreementObj.get("agreementAssetsResults"))
					.get("agreementAssetsResultList");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}

		List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			for (int i = 0; i < agreementList.size(); i++) {
				UserAgreement agreement = new UserAgreement();
				String agreementId = ((JSONObject) agreementList.get(i)).get("id").toString();
				JSONObject responseJson = null;
				try {
					responseJson = this.restApiAgreements.getAgreementInfo(accessToken, agreementId);

					if ((null != responseJson.get("workflowId"))
							&& responseJson.get("workflowId").equals(userWorkflowId)) {
						String workFlowId = (String) responseJson.get("workflowId");
						agreement.setWorkflowId(workFlowId);
						final JSONObject workflowDescription = this.restApiAgreements.workflowInfo(accessToken,
								workFlowId);

						// Parse and read response.
						String workFlowName = (String) workflowDescription.get("displayName");
						agreement.setWorkflowName(workFlowName);
						agreement.setId(agreementId);

						agreement.setName(((JSONObject) agreementList.get(i)).get("name").toString());
						agreement.setStatus(((JSONObject) agreementList.get(i)).get("status").toString());
						agreement.setModifiedDate(((JSONObject) agreementList.get(i)).get("modifiedDate").toString());
						agreement.setUserEmail(userEmail);
						userAgreementList.add(agreement);
					}
				} catch (IOException e) {
					LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
				}
			}
		}
		agreementForm.setAgreementIdList(userAgreementList);
		JSONObject searchPageInfo = (JSONObject) (((JSONObject) agreementObj.get("agreementAssetsResults"))
				.get("searchPageInfo"));
		Long nextIndex = (Long) (searchPageInfo.get("nextIndex"));
		Long totalAgreements = (long) userAgreementList.size();
		agreementForm.setNextIndex(nextIndex);

		agreementForm.setTotalAgreements(totalAgreements);

		return agreementForm;
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
			LOGGER.info("Workflow Name:: " + workflowDescription.get("name"));
			workflowInfo.setName((String) workflowDescription.get("name"));
			workflowInfo.setDisplayName((String) workflowDescription.get("displayName"));
			workflowInfo.setScope((String) workflowDescription.get("scope"));
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return workflowInfo;
	}
}
