package com.adobe.acrobatsign.service;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.adobe.acrobatsign.model.AccessTokenVO;
import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.MemberInfo;
import com.adobe.acrobatsign.model.ParticipantSet;
import com.adobe.acrobatsign.model.RefreshTokenVO;
import com.adobe.acrobatsign.model.SendAgreementVO;
import com.adobe.acrobatsign.model.SendVO;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.service.RestApiAgreements.DocumentIdentifierName;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.adobe.acrobatsign.util.RestError;
import com.adobe.acrobatsign.util.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class AdobeSignService.
 */
@Service
public class AdobeSignService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdobeSignService.class);

	/** The Constant mimeType. */
	private static final String MIME_TYPE = RestApiUtils.MimeType.PDF.toString();

	/** The integration key. */
	/*@Value(value = "${integration-key}")
	private String integrationKey;*/
	
	/** Base Token API URL. */
	@Value(value = "${tokenApiUrl}")
	private String tokenApiUrl;
	
	/** Base Refresh Token API URL. */
	@Value(value = "${refreshTokenApiUrl}")
	private String refreshTokenApiUrl;
	
	/** OAuth URL */
	@Value(value = "${oauthUrl}")
	private String oauthUrl;
	
	/** applicationId to be created by user */
	@Value(value = "${applicationId}")
	private String applicationId;
	
	@Value(value = "${applicationSecret}")
	private String applicationSecret;
	
	@Value(value = "${applicationRedirectUri}")
	private String applicationRedirectUri;

	/** The integration key. */
	@Value(value = "${agreement_status}")
	private List<String> status;

	/** The adobe sign service. */
	@Autowired
	RestApiAgreements restApiAgreements;
	
	

	public void deleteAgreements(List<UserAgreement> agreementList, String userEmail, String token) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + token;
			restApiAgreements.deleteAgreements(accessToken, agreementList, userEmail);
			LOGGER.info("Agreements Deleted Successfully");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}
	
	public void cancelReminders(List<UserAgreement> agreementList, String userEmail, String token) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + token;
			restApiAgreements.cancelReminders(accessToken, agreementList, userEmail);
			LOGGER.info("Reminder Cancelled.");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public String downloadAgreements(List<UserAgreement> agreementList, String userEmail,
			HttpServletResponse response, String token) {
		String accessToken = null;
		String combinedDocument = null;
		try {
			accessToken = Constants.BEARER + token;
			combinedDocument = restApiAgreements.downloadAgreements(accessToken, agreementList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return combinedDocument;
	}

	public ZipOutputStream downloadFormFields(List<UserAgreement> agreementList, String userEmail,
			HttpServletResponse response, String token) {
		String accessToken = null;
		ZipOutputStream zos = null;
		try {
			accessToken = Constants.BEARER + token;
			zos = restApiAgreements.downloadFormFields(accessToken, agreementList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return zos;
	}

	public List<UserAgreement> getAgreements(String userEmail, String token) {
		String accessToken = null;
		JSONArray agreementList = null;
		try {
			accessToken = Constants.BEARER + token;
			agreementList = restApiAgreements.getMyAgreements(accessToken, userEmail);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		ObjectMapper mapper = new ObjectMapper();
		List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			// Iterating JSON array
			for (int i = 0; i < agreementList.size(); i++) {
				UserAgreement userAgreement = new UserAgreement();
				userAgreement.setName(((JSONObject) agreementList.get(i)).get("name").toString());
				userAgreement.setId(((JSONObject) agreementList.get(i)).get("id").toString());
				userAgreement.setStatus(((JSONObject) agreementList.get(i)).get("status").toString());
				// Adding each element of JSON array into ArrayList
				userAgreementList.add(userAgreement);
			}
		}

		// UserAgreements userAgreementList = mapper.readValue(agreementList,
		// UserAgreements.class);
		return userAgreementList;
	}

	public AgreementInfo getContractStatus(String agreementId, String refreshTokenCode) {
		String accessToken = null;
		AgreementInfo agreementInfo = new AgreementInfo();
		try {
			accessToken = Constants.BEARER + refreshTokenCode;
			final JSONObject sendAgreementResponse = restApiAgreements.getAgreementInfo(accessToken, agreementId);

			// Parse and read response.
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info(Constants.AGREEMENT_SENT_INFO_MSG + sendAgreementResponse.get(Constants.ID));
			agreementInfo.setGroupId((String) sendAgreementResponse.get(Constants.ID));
			agreementInfo.setMessage((String) sendAgreementResponse.get("message"));
			agreementInfo.setStatus((String) sendAgreementResponse.get("status"));
			agreementInfo.setSenderEmail((String) sendAgreementResponse.get("senderEmail"));
			agreementInfo.setName((String) sendAgreementResponse.get("name"));
			List<ParticipantSet> participantSetList = new ArrayList<>();
			JSONArray participantObj = (JSONArray) sendAgreementResponse.get("participantSetsInfo");
			for (int i = 0; i < participantObj.size(); i++) {
				ParticipantSet participantSet = new ParticipantSet();
				participantSet.setRole((String) ((JSONObject) participantObj.get(i)).get("role"));
				List<MemberInfo> memberInfoObj = (List<MemberInfo>) ((JSONObject) participantObj.get(i))
						.get("memberInfos");
				participantSet.setMemberInfos(memberInfoObj);
				participantSet.setOrder(((JSONObject) participantObj.get(i)).get("order") + "");
				participantSetList.add(participantSet);
			}
			agreementInfo.setParticipantSet(participantSetList);
			// List<ParticipantSet> participantSetList = (List<ParticipantSet>)
			// mapper.readValue(sendAgreementResponse.get("participantSetsInfo").toString(),
			// ParticipantSet.class);
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return agreementInfo;
	}

	/**
	 * Gets the integration key.
	 *
	 * @return the integration key
	 */
	/*public String getIntegrationKey() {
		return this.integrationKey;
	}*/

	/**
	 * Gets the send agreement obj.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @return the send agreement obj
	 */
	private SendVO getSendAgreementObj(SendAgreementVO sendAgreementVO) {
		final SendVO sendObj = new SendVO();

		final List<ParticipantSet> participantList = new ArrayList<>();

		final List<MemberInfo> sendermemberList = new ArrayList<>();
		int count = 1;
		if ((null != sendAgreementVO.getApproverEmail()) && (sendAgreementVO.getApproverEmail().length() > 0)) {
			final List<MemberInfo> approvermemberList = new ArrayList<>();
			final ParticipantSet approverSet = new ParticipantSet();
			final MemberInfo approverInfo = new MemberInfo();
			approverInfo.setEmail(sendAgreementVO.getApproverEmail());
			approvermemberList.add(approverInfo);
			approverSet.setRole(Constants.signRole.APPROVER.toString());
			approverSet.setOrder(String.valueOf(count));
			approverSet.setMemberInfos(approvermemberList);
			participantList.add(approverSet);
			count++;
		}
		final MemberInfo senderInfo = new MemberInfo();
		senderInfo.setEmail(sendAgreementVO.getSignerEmail());
		sendermemberList.add(senderInfo);

		final ParticipantSet senderSet = new ParticipantSet();
		senderSet.setMemberInfos(sendermemberList);

		senderSet.setRole(Constants.signRole.SIGNER.toString());
		senderSet.setOrder(String.valueOf(count));
		participantList.add(senderSet);
		sendObj.setState("IN_PROCESS");
		sendObj.setMessage(sendAgreementVO.getMessage());
		sendObj.setName(sendAgreementVO.getName());
		sendObj.setParticipantSetsInfo(participantList);

		return sendObj;
	}

	/**
	 * Gets the send agreement obj.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @return the send agreement obj
	 */
	private SendVO getSendVO(org.json.JSONArray jsonArray) {
		final SendVO sendObj = new SendVO();

		final List<ParticipantSet> participantList = new ArrayList<>();
		int count = 1;
		for (int i = 0; i < jsonArray.length(); i++) {
			final List<MemberInfo> memberInfoList = new ArrayList<>();
			final ParticipantSet participantset = new ParticipantSet();
			final MemberInfo memberInfo = new MemberInfo();
			memberInfo.setEmail(((org.json.JSONObject) jsonArray.get(i)).get("email") + "");
			memberInfoList.add(memberInfo);

			participantset.setRole(Roles.valueOf(((org.json.JSONObject) jsonArray.get(i)).get("role") + "").toString());
			participantset.setOrder(String.valueOf(count));
			participantset.setMemberInfos(memberInfoList);
			participantset.setOrder(String.valueOf(count));
			participantList.add(participantset);
		}

		sendObj.setMessage("Please Sign");
		sendObj.setName("Test Agreement");
		sendObj.setState("AUTHORING");
		sendObj.setParticipantSetsInfo(participantList);

		return sendObj;
	}

	public void hideAgreements(List<UserAgreement> agreementList, String token) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + token;
			restApiAgreements.hideAgreements(accessToken, agreementList);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}
	
	public Page<UserAgreement> findPaginated(Pageable pageable, List<UserAgreement> agreementList) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<UserAgreement> list;

        if (agreementList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, agreementList.size());
            list = agreementList.subList(startItem, toIndex);
        }

        Page<UserAgreement> agreementPage
          = new PageImpl<UserAgreement>(list, PageRequest.of(currentPage, pageSize), agreementList.size());

        return agreementPage;
    }
	public AgreementForm searchAgreements(String userEmail,String startDate, String beforeDate, Integer size, String refreshTokenCode) {
		String accessToken = null;
		JSONArray agreementList = null;
		JSONObject agreementObj = null;
		AgreementForm agreementForm = new AgreementForm();
		try {
			accessToken = Constants.BEARER + refreshTokenCode;
			agreementObj = restApiAgreements.getAgreements(accessToken, userEmail, startDate, beforeDate, status, size);
			agreementList = (JSONArray) ((JSONObject) agreementObj.get("agreementAssetsResults")).get("agreementAssetsResultList");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		ObjectMapper mapper = new ObjectMapper();

		List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			for (int i = 0; i < agreementList.size(); i++) {
				UserAgreement agreement = new UserAgreement();
				agreement.setId(((JSONObject) agreementList.get(i)).get("id").toString());
				agreement.setName(((JSONObject) agreementList.get(i)).get("name").toString());
				agreement.setStatus(((JSONObject) agreementList.get(i)).get("status").toString());
				agreement.setModifiedDate(((JSONObject) agreementList.get(i)).get("modifiedDate").toString());
				agreement.setUserEmail(userEmail);
				userAgreementList.add(agreement);
			}
		}
		agreementForm.setAgreementIdList(userAgreementList);
		JSONObject searchPageInfo = (JSONObject)(((JSONObject) agreementObj.get("agreementAssetsResults")).get("searchPageInfo"));
		Long nextIndex = (Long)(searchPageInfo.get("nextIndex"));
		Long totalAgreements = (Long)(agreementObj.get("totalHits"));
		agreementForm.setNextIndex(nextIndex);
		
		agreementForm.setTotalAgreements(totalAgreements);

		// UserAgreements userAgreementList = mapper.readValue(agreementList,
		// UserAgreements.class);
		return agreementForm;
	}

	/**
	 * Send contract.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @param file1           the file 1
	 * @param refreshTokenCode 
	 * @return the string
	 */
	public String sendAgreement(org.json.JSONArray jsonArray, MultipartFile file1, String refreshTokenCode) {
		final String filePathStr = "output/";
		final String fileName = file1.getOriginalFilename();

		String accessToken = null;
		String agreementId = null;
		try {
			final Path filepath = Paths.get(filePathStr, fileName);
			try (OutputStream os = Files.newOutputStream(filepath)) {
				os.write(file1.getBytes());
			}
			final File file = new File(filePathStr + fileName);
			accessToken = Constants.BEARER + refreshTokenCode;
			final JSONObject uploadDocumentResponse = restApiAgreements.postTransientDocument(accessToken, MIME_TYPE,
					file.getAbsolutePath(), fileName);
			final String transientDocumentId = (String) uploadDocumentResponse
					.get(DocumentIdentifierName.TRANSIENT_DOCUMENT_ID.toString());

			// Send an agreement using the transient document ID derived from above.
			final DocumentIdentifierName idName = DocumentIdentifierName.TRANSIENT_DOCUMENT_ID;
			final ObjectMapper mapper = new ObjectMapper();
			final JSONObject requestJson = mapper.convertValue(this.getSendVO(jsonArray), JSONObject.class);

			final JSONObject sendAgreementResponse = restApiAgreements.sendAgreement(accessToken, requestJson,
					transientDocumentId, idName);

			// Parse and read response.
			LOGGER.info(Constants.AGREEMENT_SENT_INFO_MSG + sendAgreementResponse.get(Constants.ID));
			agreementId = (String) sendAgreementResponse.get(Constants.ID);
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return agreementId;
	}

	/**
	 * Send contract.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @param file1           the file 1
	 * @param refreshTokenCode 
	 * @return the string
	 */
	public String sendContract(SendAgreementVO sendAgreementVO, MultipartFile file1, String token) {
		final String filePathStr = "output/";
		final String fileName = file1.getOriginalFilename();

		String accessToken = null;
		String agreementId = null;
		try {
			final Path filepath = Paths.get(filePathStr, fileName);
			try (OutputStream os = Files.newOutputStream(filepath)) {
				os.write(file1.getBytes());
			}
			final File file = new File(filePathStr + fileName);
			accessToken = Constants.BEARER + token;
			final JSONObject uploadDocumentResponse = restApiAgreements.postTransientDocument(accessToken, MIME_TYPE,
					file.getAbsolutePath(), fileName);
			final String transientDocumentId = (String) uploadDocumentResponse
					.get(DocumentIdentifierName.TRANSIENT_DOCUMENT_ID.toString());

			// Send an agreement using the transient document ID derived from above.
			final DocumentIdentifierName idName = DocumentIdentifierName.TRANSIENT_DOCUMENT_ID;
			final ObjectMapper mapper = new ObjectMapper();
			final JSONObject requestJson = mapper.convertValue(this.getSendAgreementObj(sendAgreementVO),
					JSONObject.class);

			final JSONObject sendAgreementResponse = restApiAgreements.sendAgreement(accessToken, requestJson,
					transientDocumentId, idName);

			// Parse and read response.
			LOGGER.info(Constants.AGREEMENT_SENT_INFO_MSG + sendAgreementResponse.get(Constants.ID));
			agreementId = (String) sendAgreementResponse.get(Constants.ID);
		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return agreementId;
	}

	/**
	 * Sets the integration key.
	 *
	 * @param integrationKey the new integration key
	 */
	/*public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
	}*/

	public MultiValueMap<String, String> generateJsonInput(AccessTokenVO accessTokenVO) {
		
		   MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		   body.add("grant_type", accessTokenVO.getGrant_type());
		   body.add("client_id", accessTokenVO.getClient_id());
		   body.add("client_secret", accessTokenVO.getClient_secret());
		   body.add("redirect_uri", accessTokenVO.getRedirect_uri());
		   body.add("code", accessTokenVO.getCode());
		   return body;

	}
	
	private MultiValueMap<String, String> generateJsonRefreshInput(RefreshTokenVO refreshTokenVO) {
		// TODO Auto-generated method stub
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		   body.add("grant_type", refreshTokenVO.getGrant_type());
		   body.add("client_id", refreshTokenVO.getClient_id());
		   body.add("client_secret", refreshTokenVO.getClient_secret());
		   body.add("refresh_token", refreshTokenVO.getRefresh_token());
		   return body;
	}
	
	public String callApi(AccessTokenVO accessTokenVO) {
		String refreshToken =null;
		accessTokenVO.setGrant_type("authorization_code");
		accessTokenVO.setClient_id(this.applicationId);
		accessTokenVO.setClient_secret(this.applicationSecret);
		accessTokenVO.setRedirect_uri(this.applicationRedirectUri);
		
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add("Cache-Control", "no-cache");
		restHeader.add("Postman-Token", null);
		restHeader.add("Content-Type", "application/x-www-form-urlencoded");
		restHeader.add("Content-Length", null);
		restHeader.add("Host", null);

			try {
				//response = (JSONObject)RestApiUtils.makeApiCallwithResponse(tokenApiUrl, RestApiUtils.HttpRequestMethod.POST, headers,generateJsonInput(accessTokenVO));
				refreshToken = restApiAgreements.getRefreshToken(tokenApiUrl, restHeader,generateJsonInput(accessTokenVO));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("API Refresh Token Pre-requsite service "+refreshToken);
		return refreshToken;
	}

	public String getURL() {
		// TODO Auto-generated method stub
		String applicationId=this.applicationId;
		String oAuth_url = this.oauthUrl;
		String finalURL = oAuth_url.replace("CLIENTID", applicationId);
		System.out.println("Final URL "+finalURL);
		return finalURL;
	}

	public String callRefreshApi(RefreshTokenVO refreshTokenVO) {
		// TODO Auto-generated method stub
		refreshTokenVO.setGrant_type("refresh_token");
		refreshTokenVO.setClient_id(this.applicationId);
		refreshTokenVO.setClient_secret(this.applicationSecret);
		
		String refreshApitoken = null;
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add("Cache-Control", "no-cache");
		restHeader.add("Postman-Token", null);
		restHeader.add("Content-Type", "application/x-www-form-urlencoded");
		restHeader.add("Content-Length", null);
		restHeader.add("Host", null);
		
		try {
			//response = (JSONObject)RestApiUtils.makeApiCallwithResponse(tokenApiUrl, RestApiUtils.HttpRequestMethod.POST, headers,generateJsonInput(accessTokenVO));
			refreshApitoken = restApiAgreements.getRefreshApiResponse(refreshTokenApiUrl, restHeader,generateJsonRefreshInput(refreshTokenVO));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Refresh Token Service "+refreshApitoken);
	return refreshApitoken;
	}


}
