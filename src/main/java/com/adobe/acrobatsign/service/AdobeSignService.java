package com.adobe.acrobatsign.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.MemberInfo;
import com.adobe.acrobatsign.model.MultiUserAgreementDetails;
import com.adobe.acrobatsign.model.ParticipantSet;
import com.adobe.acrobatsign.model.SendAgreementVO;
import com.adobe.acrobatsign.model.SendVO;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.WorkflowDescription;
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
	@Value(value = "${integration-key}")
	private String integrationKey;

	/** The integration key. */
	@Value(value = "${agreement_status}")
	private List<String> status;

	/** The adobe sign service. */
	@Autowired
	RestApiAgreements restApiAgreements;

	private AgreementInfo agreementInfoMapper(JSONObject agreementInfoObj) {
		final AgreementInfo agreementInfo = new AgreementInfo();
		agreementInfo.setGroupId((String) agreementInfoObj.get(Constants.ID));
		agreementInfo.setMessage((String) agreementInfoObj.get("message"));
		agreementInfo.setStatus((String) agreementInfoObj.get("status"));
		agreementInfo.setSenderEmail((String) agreementInfoObj.get("senderEmail"));
		agreementInfo.setName((String) agreementInfoObj.get("name"));
		agreementInfo.setModifiedDate((String) agreementInfoObj.get("lastEventDate"));
		final List<ParticipantSet> participantSetList = new ArrayList<>();
		final JSONArray participantObj = (JSONArray) agreementInfoObj.get("participantSetsInfo");
		for (int i = 0; i < participantObj.size(); i++) {
			final ParticipantSet participantSet = new ParticipantSet();
			participantSet.setRole((String) ((JSONObject) participantObj.get(i)).get("role"));
			final List<MemberInfo> memberInfoObj = (List<MemberInfo>) ((JSONObject) participantObj.get(i))
					.get("memberInfos");
			participantSet.setMemberInfos(memberInfoObj);
			participantSet.setOrder(((JSONObject) participantObj.get(i)).get("order") + "");
			participantSetList.add(participantSet);
		}
		agreementInfo.setParticipantSet(participantSetList);
		return agreementInfo;
	}

	public AgreementForm agreementWithWorkflow(String userEmail, String startDate, String beforeDate, Integer size) {
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
		ObjectMapper mapper = new ObjectMapper();

		List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			for (int i = 0; i < agreementList.size(); i++) {
				UserAgreement agreement = new UserAgreement();
				String agreementId = ((JSONObject) agreementList.get(i)).get("id").toString();
				JSONObject responseJson = null;
				try {
					responseJson = this.restApiAgreements.getAgreementInfo(accessToken, agreementId);

				} catch (IOException e) {
					LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
				}
				if (null != responseJson.get("workflowId")) {
					agreement.setWorkflowId((String) responseJson.get("workflowId"));
					agreement.setId(agreementId);

					agreement.setName(((JSONObject) agreementList.get(i)).get("name").toString());
					agreement.setStatus(((JSONObject) agreementList.get(i)).get("status").toString());
					agreement.setModifiedDate(((JSONObject) agreementList.get(i)).get("modifiedDate").toString());
					agreement.setUserEmail(userEmail);
					userAgreementList.add(agreement);
				}
			}
		}
		agreementForm.setAgreementIdList(userAgreementList);
		JSONObject searchPageInfo = (JSONObject) (((JSONObject) agreementObj.get("agreementAssetsResults"))
				.get("searchPageInfo"));
		Long nextIndex = (Long) (searchPageInfo.get("nextIndex"));
		Long totalAgreements = (Long) (agreementObj.get("totalHits"));
		agreementForm.setNextIndex(nextIndex);

		agreementForm.setTotalAgreements(totalAgreements);

		// UserAgreements userAgreementList = mapper.readValue(agreementList,
		// UserAgreements.class);
		return agreementForm;
	}

	public void cancelReminders(List<UserAgreement> agreementList, String userEmail) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.cancelReminders(accessToken, agreementList, userEmail);
			LOGGER.info("Reminder Cancelled.");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public void cancelAgreements(List<UserAgreement> agreementList, String userEmail) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.cancelAgreements(accessToken, agreementList, userEmail);
			LOGGER.info("Agreements Cancelled.");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public void deleteAgreements(List<UserAgreement> agreementList, String userEmail) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.deleteAgreements(accessToken, agreementList, userEmail);
			LOGGER.info("Agreements Deleted Successfully");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public String downloadAgreements(List<UserAgreement> agreementList, String userEmail,
			HttpServletResponse response) {
		String accessToken = null;
		String combinedDocument = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			combinedDocument = restApiAgreements.downloadAgreements(accessToken, agreementList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return combinedDocument;
	}

	public ZipOutputStream downloadFormFields(List<UserAgreement> agreementList, String userEmail,
			HttpServletResponse response) {
		String accessToken = null;
		ZipOutputStream zos = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			zos = restApiAgreements.downloadFormFields(accessToken, agreementList, userEmail, response);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		return zos;
	}

	public Page<UserAgreement> findPaginated(Pageable pageable, List<UserAgreement> agreementList) {
		final int pageSize = pageable.getPageSize();
		final int currentPage = pageable.getPageNumber();
		final int startItem = currentPage * pageSize;
		List<UserAgreement> list;

		if (agreementList.size() < startItem) {
			list = Collections.emptyList();
		} else {
			final int toIndex = Math.min(startItem + pageSize, agreementList.size());
			list = agreementList.subList(startItem, toIndex);
		}

		final Page<UserAgreement> agreementPage = new PageImpl<UserAgreement>(list,
				PageRequest.of(currentPage, pageSize), agreementList.size());

		return agreementPage;
	}

	public List<UserAgreement> getAgreements(String userEmail) {
		String accessToken = null;
		JSONArray agreementList = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			agreementList = restApiAgreements.getMyAgreements(accessToken, userEmail);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}
		final ObjectMapper mapper = new ObjectMapper();
		final List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			// Iterating JSON array
			for (int i = 0; i < agreementList.size(); i++) {
				final UserAgreement userAgreement = new UserAgreement();
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

	public AgreementInfo getContractStatus(String agreementId) {
		String accessToken = null;
		AgreementInfo agreementInfo = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			final JSONObject sendAgreementResponse = restApiAgreements.getAgreementInfo(accessToken, agreementId);

			// Parse and read response.
			final ObjectMapper mapper = new ObjectMapper();
			LOGGER.info(Constants.AGREEMENT_SENT_INFO_MSG + sendAgreementResponse.get(Constants.ID));
			agreementInfo = agreementInfoMapper(sendAgreementResponse);
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
	public String getIntegrationKey() {
		return integrationKey;
	}

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
		if (null != sendAgreementVO.getApproverEmail() && sendAgreementVO.getApproverEmail().length() > 0) {
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
		final int count = 1;
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

	public void hideAgreements(List<UserAgreement> agreementList) {
		String accessToken = null;
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			restApiAgreements.hideAgreements(accessToken, agreementList);

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.getMessage());
		}

	}

	public AgreementForm searchAgreements(String userEmail, String startDate, String beforeDate, Integer size) {
		String accessToken = null;
		JSONArray agreementList = null;
		JSONObject agreementObj = null;
		final AgreementForm agreementForm = new AgreementForm();
		try {
			accessToken = Constants.BEARER + getIntegrationKey();
			agreementObj = restApiAgreements.getAgreements(accessToken, userEmail, startDate, beforeDate, status, size);
			agreementList = (JSONArray) ((JSONObject) agreementObj.get("agreementAssetsResults"))
					.get("agreementAssetsResultList");

		} catch (final Exception e) {
			LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
		}
		final ObjectMapper mapper = new ObjectMapper();

		final List<UserAgreement> userAgreementList = new ArrayList<>();
		if (agreementList != null) {

			for (int i = 0; i < agreementList.size(); i++) {
				final UserAgreement agreement = new UserAgreement();
				agreement.setId(((JSONObject) agreementList.get(i)).get("id").toString());
				agreement.setName(((JSONObject) agreementList.get(i)).get("name").toString());
				agreement.setStatus(((JSONObject) agreementList.get(i)).get("status").toString());
				agreement.setModifiedDate(((JSONObject) agreementList.get(i)).get("modifiedDate").toString());
				agreement.setUserEmail(userEmail);
				userAgreementList.add(agreement);
			}
		}
		agreementForm.setAgreementIdList(userAgreementList);
		final JSONObject searchPageInfo = (JSONObject) ((JSONObject) agreementObj.get("agreementAssetsResults"))
				.get("searchPageInfo");
		final Long nextIndex = (Long) searchPageInfo.get("nextIndex");
		final Long totalAgreements = (Long) agreementObj.get("totalHits");
		agreementForm.setNextIndex(nextIndex);

		agreementForm.setTotalAgreements(totalAgreements);

		// UserAgreements userAgreementList = mapper.readValue(agreementList,
		// UserAgreements.class);
		return agreementForm;
	}

	public List<UserAgreement> searchAgreementsForIds(List<String> agreementId) {
		final String accessToken = Constants.BEARER + getIntegrationKey();
		JSONObject agreementObj = null;
		final List<UserAgreement> agreementInfoList = new ArrayList<>();
		for (int i = 1; i < agreementId.size(); i++) {
			try {
				agreementObj = restApiAgreements.getAgreementInfo(accessToken, agreementId.get(i));
				final UserAgreement agreementInfo = new UserAgreement();
				agreementInfo.setStatus((String) agreementObj.get("status"));
				agreementInfo.setUserEmail((String) agreementObj.get("senderEmail"));
				agreementInfo.setName((String) agreementObj.get("name"));
				agreementInfo.setModifiedDate((String) agreementObj.get("lastEventDate"));

				agreementInfo.setId(agreementId.get(i));
				agreementInfoList.add(agreementInfo);

			} catch (final Exception e) {
				LOGGER.error(RestError.OPERATION_EXECUTION_ERROR.errMessage, e.fillInStackTrace());
			}

		}
		return agreementInfoList;
	}

	public MultiUserAgreementDetails searchMultiUserAgreements(List<String> userEmails, String startDate,
			String beforeDate, Integer size) {
		AgreementForm agreementForm = new AgreementForm();
		Long totalAgreements = 0L;
		final MultiUserAgreementDetails multiUserAgreementDetails = new MultiUserAgreementDetails();
		final Map<String, Long> nextIndexMap = new HashMap<>();
		final List<UserAgreement> allAgreements = new ArrayList<>();
		final List<String> userIds = new ArrayList<>();
		userIds.addAll(userEmails);
		for (int i = 1; i < userIds.size(); i++) {
			agreementForm = searchAgreements(userIds.get(i), startDate, beforeDate, size);
			totalAgreements = agreementForm.getTotalAgreements();
			if (agreementForm.getNextIndex() == null) {
				userEmails.remove(userIds.get(i));
			} else {
				nextIndexMap.put(userIds.get(i), agreementForm.getNextIndex());
			}
			allAgreements.addAll(agreementForm.getAgreementIdList());

		}
		multiUserAgreementDetails.setTotalAgreements(totalAgreements);
		multiUserAgreementDetails.setUserEmails(userEmails);
		multiUserAgreementDetails.setAgreementList(allAgreements);
		multiUserAgreementDetails.setNextIndexMap(nextIndexMap);
		return multiUserAgreementDetails;
	}

	public MultiUserAgreementDetails searchMultiUserAgreements(List<String> userEmails, String startDate,
			String beforeDate, Map<String, Integer> nextIndexMap) {
		AgreementForm agreementForm = new AgreementForm();
		Long totalAgreements = 0L;
		final MultiUserAgreementDetails multiUserAgreementDetails = new MultiUserAgreementDetails();
		final Map<String, Long> nextIndexMapVal = new HashMap<>();
		final List<UserAgreement> allAgreements = new ArrayList<>();
		final List<String> userIds = new ArrayList<>();
		userIds.addAll(userEmails);
		for (int i = 1; i < userIds.size(); i++) {
			agreementForm = searchAgreements(userIds.get(i), startDate, beforeDate, nextIndexMap.get(userIds.get(i)));
			totalAgreements = agreementForm.getTotalAgreements();
			if (agreementForm.getNextIndex() == null) {
				userEmails.remove(userIds.get(i));
			} else {
				nextIndexMapVal.put(userIds.get(i), agreementForm.getNextIndex());
			}
			allAgreements.addAll(agreementForm.getAgreementIdList());

		}
		multiUserAgreementDetails.setTotalAgreements(totalAgreements);
		multiUserAgreementDetails.setUserEmails(userEmails);
		multiUserAgreementDetails.setAgreementList(allAgreements);
		multiUserAgreementDetails.setNextIndexMap(nextIndexMapVal);
		return multiUserAgreementDetails;
	}

	/**
	 * Send contract.
	 *
	 * @param sendAgreementVO the send agreement VO
	 * @param file1           the file 1
	 * @return the string
	 */
	public String sendAgreement(org.json.JSONArray jsonArray, MultipartFile file1) {
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
			accessToken = Constants.BEARER + getIntegrationKey();
			final JSONObject uploadDocumentResponse = restApiAgreements.postTransientDocument(accessToken, MIME_TYPE,
					file.getAbsolutePath(), fileName);
			final String transientDocumentId = (String) uploadDocumentResponse
					.get(DocumentIdentifierName.TRANSIENT_DOCUMENT_ID.toString());

			// Send an agreement using the transient document ID derived from above.
			final DocumentIdentifierName idName = DocumentIdentifierName.TRANSIENT_DOCUMENT_ID;
			final ObjectMapper mapper = new ObjectMapper();
			final JSONObject requestJson = mapper.convertValue(getSendVO(jsonArray), JSONObject.class);

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
	 * @return the string
	 */
	public String sendContract(SendAgreementVO sendAgreementVO, MultipartFile file1) {
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
			accessToken = Constants.BEARER + getIntegrationKey();
			final JSONObject uploadDocumentResponse = restApiAgreements.postTransientDocument(accessToken, MIME_TYPE,
					file.getAbsolutePath(), fileName);
			final String transientDocumentId = (String) uploadDocumentResponse
					.get(DocumentIdentifierName.TRANSIENT_DOCUMENT_ID.toString());

			// Send an agreement using the transient document ID derived from above.
			final DocumentIdentifierName idName = DocumentIdentifierName.TRANSIENT_DOCUMENT_ID;
			final ObjectMapper mapper = new ObjectMapper();
			final JSONObject requestJson = mapper.convertValue(getSendAgreementObj(sendAgreementVO), JSONObject.class);

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
	public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
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
