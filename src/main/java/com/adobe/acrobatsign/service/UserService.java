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

import com.adobe.acrobatsign.model.DetailedUserInfo;
import com.adobe.acrobatsign.model.UserInfo;
import com.adobe.acrobatsign.model.UsersInfo;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.adobe.acrobatsign.util.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

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

	public List<DetailedUserInfo> activeUsers() {
		UsersInfo userInfo = allUsers();
		List<UserInfo> allUserInfo = userInfo.getUserInfoList();
		List<DetailedUserInfo> activeUserList = new ArrayList<>();
		for (int i = 0; i < allUserInfo.size(); i++) {
			DetailedUserInfo detailedUserInfo = detailedUserInfo(allUserInfo.get(i).getId());
			if (Constants.StatusEnum.ACTIVE.getValue().equals(detailedUserInfo.getStatus().getValue())) {
				activeUserList.add(detailedUserInfo);
			}
		}
		return activeUserList;
	}

	public UsersInfo allUsers() {
		String accessToken = null;
		UsersInfo userInfoList = new UsersInfo();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_ALL_USERS;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
			JSONObject userInfoObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
					RestApiUtils.HttpRequestMethod.GET, headers);
			userInfoList = objectMapper.readValue(userInfoObj.toJSONString(), UsersInfo.class);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

		return userInfoList;
	}

	public DetailedUserInfo detailedUserInfo(String userId) {
		String accessToken = null;
		DetailedUserInfo detailedUserInfo = new DetailedUserInfo();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_ALL_USERS + "/" + userId;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
			JSONObject userInfoObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
					RestApiUtils.HttpRequestMethod.GET, headers);
			detailedUserInfo = objectMapper.readValue(userInfoObj.toJSONString(), DetailedUserInfo.class);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return detailedUserInfo;
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

	public UsersInfo userInfo() {
		String accessToken = null;
		UsersInfo userInfoList = new UsersInfo();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();

			final String endpointUrl = getBaseURL() + Constants.GET_ALL_USERS;

			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			ObjectMapper objectMapper = new ObjectMapper();
			JSONObject userInfoObj = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
					RestApiUtils.HttpRequestMethod.GET, headers);
			userInfoList = objectMapper.readValue(userInfoObj.toJSONString(), UsersInfo.class);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

		return userInfoList;
	}

}
