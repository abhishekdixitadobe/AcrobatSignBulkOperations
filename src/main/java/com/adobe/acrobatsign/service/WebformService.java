package com.adobe.acrobatsign.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.MultiUserWidgetDetails;
import com.adobe.acrobatsign.model.UserAgreements;
import com.adobe.acrobatsign.model.UserWidget;
import com.adobe.acrobatsign.model.UserWidgets;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.adobe.acrobatsign.util.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebformService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebformService.class);

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

	@Autowired
	UserService userService;

	public AgreementForm agreementsForWebforms(String widgetId, String email) {
		String accessToken = Constants.BEARER + getIntegrationKey();
		UserAgreements userAgreement = new UserAgreements();
		final AgreementForm agreementForm = new AgreementForm();
		final String endpointUrl = getBaseURL() + Constants.GET_WIDGET;
		try {
			final StringBuilder urlString = new StringBuilder();
			urlString.append(endpointUrl).append("/").append(widgetId).append("/").append(Constants.AGREEMENTS);
			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + email);
			// Invoke API and get JSON response.
			JSONObject responseJson = null;
			responseJson = (JSONObject) RestApiUtils.makeApiCall(urlString.toString(),
					RestApiUtils.HttpRequestMethod.GET, headers);
			ObjectMapper objectMapper = new ObjectMapper();
			userAgreement = objectMapper.readValue(responseJson.toJSONString(), UserAgreements.class);

		} catch (Exception e) {

		}
		if (null != userAgreement && null != userAgreement.getUserAgreementList()
				&& userAgreement.getUserAgreementList().size() > 0) {
			agreementForm.setAgreementIdList(userAgreement.getUserAgreementList());
		}
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

	public MultiUserWidgetDetails getWebforms(List<String> activeUserList) {
		String accessToken = null;
		UserWidgets userWidgets = new UserWidgets();
		final MultiUserWidgetDetails multiUserWidgetDetails = new MultiUserWidgetDetails();
		final List<String> userIds = new ArrayList<>();
		final Map<String, String> nextIndexMapVal = new HashMap<>();
		List<UserWidget> allUserWidgetList = new ArrayList<>();

		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_WIDGET;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
			for (int i = 0; i < activeUserList.size(); i++) {
				userIds.add(activeUserList.get(i));
				String activeUser = activeUserList.get(i);
				if (null != activeUserList.get(i)) {
					userIds.add(activeUserList.get(i));
					headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + activeUserList.get(i));
				}
				JSONObject widgetsObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
						RestApiUtils.HttpRequestMethod.GET, headers);

				userWidgets = objectMapper.readValue(widgetsObj.toJSONString(), UserWidgets.class);
				if (null != userWidgets) {
					if (null != userWidgets.getUserWidgetList() && userWidgets.getUserWidgetList().size() > 0) {
						List<UserWidget> userWidgetList = userWidgets.getUserWidgetList();
						for (int j = 0; j < userWidgetList.size(); j++) {
							StringBuffer wigetIdUrl = new StringBuffer();
							wigetIdUrl.append(endpointUrl).append("/").append(userWidgetList.get(j).getId());
							JSONObject widgetInfoObj = (JSONObject) RestApiUtils.makeApiCall(wigetIdUrl.toString(),
									RestApiUtils.HttpRequestMethod.GET, headers);
							String ownerEmail = (String) widgetInfoObj.get("ownerEmail");
							if (ownerEmail.equals(activeUser)) {
								userWidgetList.get(j).setOwnerEmail(ownerEmail);
								allUserWidgetList.add(userWidgetList.get(j));
							}
						}
					}

					if (userWidgets.getPage().getNextCursor() == null) {
						userIds.remove(activeUserList.get(i));
					} else {
						nextIndexMapVal.put(activeUserList.get(i), userWidgets.getPage().getNextCursor());
					}
				}
			}

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		multiUserWidgetDetails.setUserEmails(userIds);
		multiUserWidgetDetails.setWidgetList(allUserWidgetList);
		multiUserWidgetDetails.setNextIndexMap(nextIndexMapVal);
		return multiUserWidgetDetails;
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
}
