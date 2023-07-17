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

	public AgreementForm agreementWithWorkflow(List<String> activeUsers, String startDate, String beforeDate,
			Integer size, String userWorkflowId) {
		String accessToken = null;
		JSONArray agreementList = null;
		JSONArray agreementJSONList = null;
		JSONObject agreementObj = null;
		AgreementForm agreementForm = new AgreementForm();
		Map<String, JSONArray> agreementListMap = new HashMap<>();

		final List<String> userIds = new ArrayList<>();
		userIds.addAll(activeUsers);

		try {
			accessToken = Constants.BEARER + this.getIntegrationKey();
			for (int i = 0; i < activeUsers.size(); i++) {
				agreementJSONList = new JSONArray();
				agreementObj = this.restApiAgreements.getAgreements(accessToken, activeUsers.get(i), startDate,
						beforeDate, this.status, size);
				agreementList = (JSONArray) ((JSONObject) agreementObj.get(Constants.AGREEMENT_ASSETS_RESULTS))
						.get(Constants.AGREEMENT_ASSETS_RESULT_LIST);
				if ((null != agreementObj) && (null != agreementList) && (agreementList.size() > 0)
						&& (null != agreementObj.get(Constants.TOTAL_HITS))
						&& ((Long) agreementObj.get(Constants.TOTAL_HITS) > 50)) {
					final JSONObject searchPageInfo = (JSONObject) ((JSONObject) agreementObj
							.get(Constants.AGREEMENT_ASSETS_RESULTS)).get(Constants.SEARCH_PAGE_INFO);
					Long nextIndex = (Long) searchPageInfo.get(Constants.NEXT_INDEX);
					while (nextIndex != null) {
						agreementObj = this.restApiAgreements.getAgreements(accessToken, activeUsers.get(i), startDate,
								beforeDate, this.status, nextIndex.intValue());
						final JSONObject searchPageObj = (JSONObject) ((JSONObject) agreementObj
								.get(Constants.AGREEMENT_ASSETS_RESULTS)).get(Constants.SEARCH_PAGE_INFO);
						nextIndex = (Long) searchPageObj.get(Constants.NEXT_INDEX);
						agreementList
								.addAll((JSONArray) ((JSONObject) agreementObj.get(Constants.AGREEMENT_ASSETS_RESULTS))
										.get(Constants.AGREEMENT_ASSETS_RESULT_LIST));

					}
				}
				if ((null != agreementList) && (agreementList.size() > 0)) {
					agreementJSONList.addAll(agreementList);
					agreementListMap.put(activeUsers.get(i), agreementJSONList);
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}

		List<UserAgreement> userAgreementList = new ArrayList<>();

		Set<String> userKey = agreementListMap.keySet();
		Iterator<String> userIterator = userKey.iterator();
		while (userIterator.hasNext()) {
			String user = userIterator.next();
			JSONArray usersAgreementArray = agreementListMap.get(user);
			if (usersAgreementArray.size() > 0) {
				for (int i = 0; i < usersAgreementArray.size(); i++) {
					UserAgreement agreement = new UserAgreement();
					String agreementId = ((JSONObject) usersAgreementArray.get(i)).get(Constants.ID).toString();
					JSONObject responseJson = null;
					try {
						responseJson = this.restApiAgreements.getAgreementInfo(accessToken, agreementId);

						if ((null != responseJson) && (null != responseJson.get(Constants.WORKFLOW_ID))
								&& responseJson.get(Constants.WORKFLOW_ID).equals(userWorkflowId)) {
							String workFlowId = (String) responseJson.get(Constants.WORKFLOW_ID);
							agreement.setWorkflowId(workFlowId);
							final JSONObject workflowDescription = this.restApiAgreements.workflowInfo(accessToken,
									workFlowId);

							// Parse and read response.
							String workFlowName = (String) workflowDescription.get(Constants.DISPLAY_NAME);
							agreement.setWorkflowName(workFlowName);
							agreement.setId(agreementId);

							agreement.setName(((JSONObject) usersAgreementArray.get(i)).get(Constants.NAME).toString());
							agreement.setStatus(
									((JSONObject) usersAgreementArray.get(i)).get(Constants.STATUS).toString());
							agreement.setModifiedDate(
									((JSONObject) usersAgreementArray.get(i)).get(Constants.MODIFIED_DATE).toString());
							agreement.setUserEmail(user);
							userAgreementList.add(agreement);
						}
					} catch (IOException e) {

						e.printStackTrace();
						LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getCause());
					}
				}
			}
		}
		agreementForm.setAgreementIdList(userAgreementList);
		JSONObject searchPageInfo = (JSONObject) (((JSONObject) agreementObj.get(Constants.AGREEMENT_ASSETS_RESULTS))
				.get(Constants.SEARCH_PAGE_INFO));
		Long nextIndex = (Long) (searchPageInfo.get(Constants.NEXT_INDEX));
		Long totalSize = (long) userAgreementList.size();
		agreementForm.setNextIndex(nextIndex);

		agreementForm.setTotalAgreements(totalSize);

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
			LOGGER.info("Workflow Name:: " + workflowDescription.get(Constants.NAME));
			workflowInfo.setName((String) workflowDescription.get(Constants.NAME));
			workflowInfo.setDisplayName((String) workflowDescription.get(Constants.DISPLAY_NAME));
			workflowInfo.setScope((String) workflowDescription.get(Constants.SCOPE));
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return workflowInfo;
	}
}
