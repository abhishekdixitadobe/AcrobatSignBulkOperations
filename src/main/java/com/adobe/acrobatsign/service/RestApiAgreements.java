package com.adobe.acrobatsign.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.adobe.acrobatsign.model.AgreementAssetsCriteria;
import com.adobe.acrobatsign.model.DateRange;
import com.adobe.acrobatsign.model.DateRangeFilter;
import com.adobe.acrobatsign.model.ReminderInfo;
import com.adobe.acrobatsign.model.ReminderInfo.StatusEnum;
import com.adobe.acrobatsign.model.RemindersResponse;
import com.adobe.acrobatsign.model.SearchRequestBody;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.util.FileUtils;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Encapsulates calls to REST end points related to agreements, documents and
 * related resources.
 */
@Service
public class RestApiAgreements {
	/**
	 * Represents the various ways that a set of documents can be identified,
	 * depending on the context.
	 */
	public enum DocumentIdentifierName {
		TRANSIENT_DOCUMENT_ID("transientDocumentId"), LIBRARY_DOCUMENT_ID("libraryDocumentId"),
		DOCUMENT_URL("documentURL");

		private final String actualName;

		DocumentIdentifierName(String actualName) {
			this.actualName = actualName;
		}

		@Override
		public String toString() {
			return this.actualName;
		}
	}

	// End point components used by this class.
	private static final String AGREEMENTS_ENDPOINT = "/agreements";

	private static final String WORKFLOW_ENDPOINT = "/workflows";

	private static final String GET_REMINDERS_ENDPOINT = "/reminders";

	private static final String AUDIT_ENDPOINT = "/auditTrail";
	private static final String DOCUMENTS_ENDPOINT = "/documents";
	private static final String COMBINEDDOC_ENDPOINT = "/combinedDocument";
	private static final String MEMBERS_ENDPOINT = "/members";
	private static final String REMINDERS_ENDPOINT = "/reminders";
	private static final String SIGNING_URLS_ENDPOINT = "/signingUrls";
	private static final String SEARCH_AGREEMENTS = "/search";
	private static final String TRANSIENT_DOCUMENTS_ENDPOINT = "/transientDocuments";
	private static final String BASE_URL_API_V6 = "/api/rest/v6";

	private static final String FILEINFOS = "fileInfos";

	public static final String REGEX_PATTERN = "^[^<>:\\\"/\\\\\\\\|?*]*$";

	/** The integration key. */
	@Value(value = "${agreement_type}")
	private List<String> type;

	@Value(value = "${downloadPath}")
	private String downloadPath;

	@Value(value = "${baseUrl}")
	private String baseUrl;

	@Value(value = "${user_role}")
	private List<String> role;

	@Value(value = "${visibility}")
	private String visibility;

	@SuppressWarnings("unchecked")
	public void cancelReminders(String accessToken, List<UserAgreement> agreementIdList, String userEmail)
			throws Exception {
		// URL to invoke the agreements end point.
		try {
			final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
			RestTemplate restTemplate = new RestTemplate();
			for (UserAgreement agreement : agreementIdList) {
				StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append(GET_REMINDERS_ENDPOINT);
				HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				ResponseEntity<RemindersResponse> remindersResponse = restTemplate.exchange(urlString.toString(),
						HttpMethod.GET, entity, RemindersResponse.class);
				List<ReminderInfo> reminderInfoList = remindersResponse.getBody().getReminderInfoList();
				for (ReminderInfo reminderInfo : reminderInfoList) {
					StringBuilder cancelReminderStr = new StringBuilder();
					ReminderInfo updatedReminder = new ReminderInfo();
					updatedReminder.setStatus(StatusEnum.CANCELED);
					updatedReminder.setRecipientParticipantIds(reminderInfo.getRecipientParticipantIds());
					HttpEntity<ReminderInfo> request = new HttpEntity<>(updatedReminder, restHeader);
					cancelReminderStr.append(endpointUrl).append("/").append(agreement.getId())
							.append(GET_REMINDERS_ENDPOINT).append("/").append(reminderInfo.getReminderId());
					restTemplate.exchange(cancelReminderStr.toString(), HttpMethod.PUT, request, ReminderInfo.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns a list of agreements (and their meta data) owned by the API user.
	 *
	 * @param accessToken Access token of the API user.
	 * @return JSON response containing the list of agreements.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void deleteAgreements(String accessToken, List<UserAgreement> agreementIdList, String userEmail)
			throws Exception {
		// URL to invoke the agreements end point.
		try {
			final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
			RestTemplate restTemplate = new RestTemplate();
			for (UserAgreement agreement : agreementIdList) {
				StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/documents");
				HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				restTemplate.exchange(urlString.toString(), HttpMethod.DELETE, entity, byte[].class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String downloadAgreements(String accessToken, List<UserAgreement> agreementIdList, String userEmail,
			HttpServletResponse response) {
		// URL to invoke the agreements end point.
		RestTemplate restTemplate = new RestTemplate();
		String agreementName = null;
		// Create header list.
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
			zos = new ZipOutputStream(response.getOutputStream());
			for (UserAgreement agreement : agreementIdList) {
				agreementName = agreement.getId() + "----" + agreement.getName();
				StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/combinedDocument");

				StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(this.downloadPath).append(agreement.getUserEmail());

				File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}
				// ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
				HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				ResponseEntity<byte[]> resource = null;
				boolean flag = false;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (Exception e) {
					System.out.println("Issue in Agreement name --" + agreementName);
					flag = true;
				}
				// byte[] resource = (byte[])
				// RestApiUtils.makeApiCall(url,RestApiUtils.HttpRequestMethod.GET, headers);
				if (!flag) {
					String fileName = agreement.getId();
					if (agreement.getName().matches(REGEX_PATTERN)) {
						fileName = fileName + "_" + agreement.getName();
					}
					ZipEntry entry = new ZipEntry(fileName + ".pdf");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".pdf"), resource.getBody());
				}
			}
			zos.close();
		} catch (Exception e) {
			System.out.println("Agreement name --" + agreementName);
			e.printStackTrace();
		}
		return "SUCCESS";
	}

	public ZipOutputStream downloadFormFields(String accessToken, List<UserAgreement> agreementIdList, String userEmail,
			HttpServletResponse response) {
		// URL to invoke the agreements end point.
		RestTemplate restTemplate = new RestTemplate();
		String agreementName = null;
		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
			zos = new ZipOutputStream(response.getOutputStream());
			// Invoke API and get JSON response.
			for (UserAgreement agreement : agreementIdList) {
				agreementName = agreement.getId() + "----" + agreement.getName();
				StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/formData");

				StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(this.downloadPath).append(agreement.getUserEmail());

				File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}

				HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				boolean flag = false;
				ResponseEntity<byte[]> resource = null;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (Exception e) {
					System.out.println("Issue in Agreement name --" + agreementName);
					flag = true;
				}
				if (!flag) {
					String fileName = agreement.getId();
					if (agreement.getName().matches(REGEX_PATTERN)) {
						fileName = fileName + "_" + agreement.getName();
					}
					ZipEntry entry = new ZipEntry(fileName + ".csv");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".csv"), resource.getBody());
				}
			}
			zos.close();
		} catch (Exception e) {
			System.out.println("Agreement name --" + agreementName);
			e.printStackTrace();
		}
		return zos;
	}

	/**
	 * Returns as a byte array the audit trail (in PDF format) of a specified
	 * agreement.
	 *
	 * @param accessToken Access token of the API user.
	 * @param agrId       The agreement ID.
	 * @return Byte array containing contents of the audit trail.
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] getAgreementAuditTrailBytes(String accessToken, String agrId) throws IOException {
		// URL to invoke the audit trail end-point for an agreement.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + AUDIT_ENDPOINT;

		// Create header list
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get the audit trail
		byte[] responseBytes = null;
		responseBytes = (byte[]) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseBytes;
	}

	/**
	 * <p>
	 * Combines all documents associated with an agreement, except its audit trail,
	 * into a single PDF file and returns the contents of the combined PDF file.
	 * </p>
	 * <p>
	 * Note: To include the audit trail, include "auditReport=true" in the query
	 * string of the end-point URL.
	 * </p>
	 *
	 * @param accessToken Access token of the API user.
	 * @param agrId       The agreement ID.
	 * @return Byte array containing contents of the combined PDF file.
	 * @throws Exception
	 */
	public byte[] getAgreementCombinedBytes(String accessToken, String agrId) throws IOException {
		// URL to invoke the combined document end-point for an agreement.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + COMBINEDDOC_ENDPOINT;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get the combined PDF file as a byte array.
		byte[] responseBytes = null;
		responseBytes = (byte[]) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseBytes;
	}

	/**
	 * Returns a list of all document IDs (and their meta data) associated with an
	 * agreement.
	 * <p>
	 * Note: Document IDs for the same document will be different as per their
	 * format (original vs. generated PDF). The IDs returned by default represent
	 * documents in the converted PDF format. To get the supporting documents in
	 * their original format, append "supportingDocumentContentFormat=ORIGINAL" to
	 * the query string of the end-point URL.
	 * </p>
	 *
	 * @param accessToken Access token of the API user.
	 * @param agrId       The agreement ID.
	 * @return JSON response containing the list of documents.
	 * @throws Exception
	 */
	public JSONObject getAgreementDocuments(String accessToken, String agrId) throws IOException {
		// URL to invoke the documents end-point for an agreement.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + DOCUMENTS_ENDPOINT;

		// Create header list
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

	/**
	 * Returns information about the specified agreement.
	 *
	 * @param accessToken Access token of the API user.
	 * @param agrId       The agreement ID.
	 * @return JSON response containing information about the agreement.
	 * @throws Exception
	 */
	public JSONObject getAgreementInfo(String accessToken, String agrId) throws IOException {
		// URL to invoke the agreement end point.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

	/**
	 * Returns information about the specified agreement.
	 *
	 * @param accessToken               Access token of the API user.
	 * @param agrId                     The agreement ID.
	 * @param includeNextParticipantSet Should the response have the next
	 *                                  participant set exclusively?
	 * @return JSON response containing the members of the agreement.
	 * @throws Exception
	 */
	public JSONObject getAgreementMembers(String accessToken, String agrId, boolean includeNextParticipantSet)
			throws IOException {
		// URL to invoke the agreement end point.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + MEMBERS_ENDPOINT
				+ "?includeNextParticipantSet=" + includeNextParticipantSet;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

	public JSONObject getAgreements(String accessToken, String userEmail, String startDate, String beforeDate,
			List<String> status, Integer size) throws Exception {
		// URL to invoke the agreements end point.
		final String endpointUrl = this.getBaseURL() + SEARCH_AGREEMENTS;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.ACCEPT.toString(), "application/json");
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		if (null != userEmail) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + userEmail);
		}

		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		Date formatStartDate = formatter1.parse(startDate);
		String startDateStr = formatter.format(formatStartDate);

		Date formatBeforeDate = formatter1.parse(beforeDate);
		String beforeDateStr = formatter.format(formatBeforeDate);

		DateRange dateRange = new DateRange();
		DateRangeFilter range = new DateRangeFilter();

		dateRange.setGt(startDateStr);
		dateRange.setLt(beforeDateStr);

		range.setRange(dateRange);

		JSONObject currAgreementList = null;
		SearchRequestBody searchRequestBody = new SearchRequestBody();
		List<String> scope = new ArrayList<>();
		AgreementAssetsCriteria agreementAssetsCriteria = new AgreementAssetsCriteria();
		scope.add("AGREEMENT_ASSETS");
		agreementAssetsCriteria.setStatus(status);
		agreementAssetsCriteria.setType(this.type);
		agreementAssetsCriteria.setModifiedDate(range);
		agreementAssetsCriteria.setRole(this.role);
		agreementAssetsCriteria.setVisibility(this.visibility);
		agreementAssetsCriteria.setStartIndex(size);
		searchRequestBody.setScope(scope);

		searchRequestBody.setAgreementAssetsCriteria(agreementAssetsCriteria);

		ObjectWriter searchRequestJSON = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = searchRequestJSON.writeValueAsString(searchRequestBody);
		final JSONObject myAgreements = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
				RestApiUtils.HttpRequestMethod.POST, headers, json.toString());

		if (myAgreements == null) {
			// no more agreements to fetch
			return currAgreementList;
		}
		// Display agreement ID, name and status of each agreement.
		return myAgreements;
	}

	private String getBaseURL() {
		return this.baseUrl + BASE_URL_API_V6;
	}

	/**
	 * Returns as a byte array the contents of a document associated with a
	 * specified agreement.<br>
	 * Note: The format of content can either be PDF or original, depending upon the
	 * format associated with ID.
	 *
	 * @param accessToken Access token of the API user.
	 * @param agrId       The agreement ID.
	 * @param docId       The document ID.
	 * @return Byte array containing contents of the specified document.
	 * @throws Exception
	 */
	public byte[] getDocumentBytes(String accessToken, String agrId, String docId) throws Exception {
		// URL to invoke the document end-point for an agreement.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + DOCUMENTS_ENDPOINT + "/" + docId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get bytes of file.
		byte[] responseBytes = null;
		responseBytes = (byte[]) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseBytes;
	}

	/**
	 * Returns a list of agreements (and their meta data) owned by the API user.
	 *
	 * @param accessToken Access token of the API user.
	 * @return JSON response containing the list of agreements.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getMyAgreements(String accessToken, String userEmail) throws Exception {
		// URL to invoke the agreements end point.
		final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
		String url = endpointUrl;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		if (null != userEmail) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + userEmail);
		}
		// Invoke API and get JSON response.
		final JSONArray agreementList = new JSONArray();
		String cursor = null;

		do {
			final JSONObject myAgreements = (JSONObject) RestApiUtils.makeApiCall(url,
					RestApiUtils.HttpRequestMethod.GET, headers);

			if (myAgreements == null) {
				// no more agreements to fetch
				return agreementList;
			}
			// Display agreement ID, name and status of each agreement.
			final JSONArray currAgreementList = (JSONArray) myAgreements.get("userAgreementList");

			// add the current list to response agreement list
			agreementList.addAll(currAgreementList);

			// Fetch the next page cursor
			final JSONObject page = (JSONObject) myAgreements.get("page");
			cursor = (String) page.get("nextCursor");

			// update cursor to fetch next page of agreements
			if ((cursor != null) && !cursor.trim().isEmpty()) {
				url = endpointUrl + "?cursor=" + cursor;
			}
		} while (cursor != null);

		return agreementList;
	}

	/**
	 * Returns information about the specified agreement.
	 *
	 * @param accessToken Access token of the API user(signer/sender).
	 * @param agrId       The agreement ID.
	 * @return JSON response containing signing url of the agreement.
	 * @throws Exception
	 */
	public JSONObject getSigningUrl(String accessToken, String agrId) throws Exception {
		// URL to invoke the agreement end point.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + SIGNING_URLS_ENDPOINT;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

	public void hideAgreements(String accessToken, List<UserAgreement> agreementIdList) {
		// URL to invoke the agreements end point.
		try {
			final String endpointUrl = this.getBaseURL() + AGREEMENTS_ENDPOINT;
			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
			// Invoke API and get JSON response.
			JSONObject hideJson = new JSONObject();
			hideJson.put("visibility", "HIDE");
			String cursor = null;
			RestTemplate restTemplate = new RestTemplate();
			for (UserAgreement agreement : agreementIdList) {
				StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/me/visibility");

				if (null != agreement.getUserEmail()) {
					headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + agreement.getUserEmail());
				HttpEntity<String> entity = new HttpEntity<>(hideJson.toString(), restHeader);

				restTemplate.exchange(urlString.toString(), HttpMethod.PUT, entity, byte[].class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Uploads a transient document to the Adobe Sign server, typically as part of
	 * an agreement sending workflow. A "transient document" is a temporary copy of
	 * the uploaded document which is available for a limited time after which it is
	 * deleted automatically.
	 *
	 * @param accessToken      Access token of the API user.
	 * @param mimeType         The mime type of the file to be uploaded.
	 * @param fileToBeUploaded Name of the file to be uploaded. The file must exist
	 *                         in the "requests" sub-package.
	 * @param uploadedFileName Name to be given to the uploaded file. The name can
	 *                         be different from the original name, but must consist
	 *                         only of characters in the ASCII character given this
	 *                         basic sample implementation.
	 * @return JSON response containing the ID of the uploaded transient document.
	 * @throws Exception
	 */
	public JSONObject postTransientDocument(String accessToken, String mimeType, String fileToBeUploaded,
			String uploadedFileName) throws Exception {
		final String url = this.getBaseURL() + TRANSIENT_DOCUMENTS_ENDPOINT;
		// Create header list for the request.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.ACCEPT.toString(), "application/json");

		// Meta-data associated with the file.
		final Map<String, String> fileMetaData = new HashMap<>();
		fileMetaData.put(RestApiUtils.HttpHeaderField.MIME_TYPE.toString(), mimeType);
		fileMetaData.put(RestApiUtils.HttpHeaderField.FILE_NAME.toString(), uploadedFileName);

		// Get a reference to the file to be uploaded to Adobe Sign.
		final File fileToUpload = new File(fileToBeUploaded);

		// Invoke API and get JSON response
		return (JSONObject) RestApiUtils.makeApiCall(url, headers, fileMetaData, fileToUpload);
	}

	/**
	 * Creates an agreement using the specified document, and sends it to the
	 * recipients specified in the input JSON file. Most of the agreement-sending
	 * information should be specified in the file itself. The document ID is added
	 * to the structure dynamically based on the ID name specified by the caller,
	 * which also determines the format in which to specify the document ID.
	 *
	 * @param accessToken     Access token of the API user.
	 * @param requestJsonFile Name of the file containing the JSON structure used as
	 *                        the input for this API call.
	 * @param documentId      Document ID of the document to be associated with the
	 *                        agreement. It can refer to a transient document or a
	 *                        library document.
	 * @param idName          Name by which to refer to a list containing document
	 *                        IDs when adding it to the input JSON structure. Must
	 *                        be one of
	 *                        <ul>
	 *                        <li>DocumentIdentifierName.TRANSIENT_DOCUMENT_IDS</li>
	 *                        <li>DocumentIdentifierName.LIBRARY_DOCUMENT_ID</li>
	 *                        </ul>
	 * @return JSON response containing the ID of the newly created agreement.
	 * @throws Exception
	 * @see DocumentIdentifierName
	 */
	@SuppressWarnings("unchecked")
	public JSONObject sendAgreement(String accessToken, JSONObject requestJson, String documentId,
			DocumentIdentifierName idName) throws Exception {
		// URL to invoke the agreements end point.
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT;

		// Create HTTP header list
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.JSON.toString());
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response. The ID name determines what the input
		// (request) JSON structure should contain.
		JSONObject responseJson = null;
		// JSONObject requestJson = ((new
		// ObjectMapper()).valueToTree(requestJsonFile));//FileUtils.getRequestJson(requestJsonFile);

		if (requestJson != null) {

			// Check from the input parameter "idName" whether the file type to be used in
			// sending an agreement is a library document or a
			// transient document.
			// Based on the document type retrieved from above, set the corresponding item
			// in the request JSON structure.
			if ((idName == DocumentIdentifierName.LIBRARY_DOCUMENT_ID)
					|| (idName == DocumentIdentifierName.TRANSIENT_DOCUMENT_ID)) {

				final ArrayList<JSONObject> fileInfos = new ArrayList<>();
				final JSONObject fileInfo = new JSONObject();
				fileInfo.put(idName, documentId);
				fileInfos.add(fileInfo);
				requestJson.put(FILEINFOS, fileInfos);
			}
			responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.POST, headers,
					requestJson.toString());
		}

		return responseJson;
	}

	/**
	 * Sends an email to the participants of an agreement reminding them to sign it.
	 *
	 * @param accessToken     Access token of the API user.
	 * @param requestJsonFile Name of the file containing the JSON structure used as
	 *                        the input for this API call.
	 * @param agreementId     ID of the agreement whose participants will receive
	 *                        the reminder.
	 * @return JSON response containing information about the reminder sent.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public JSONObject sendReminder(String accessToken, String requestJsonFile, String agreementId,
			List<String> participantIds) throws Exception {
		// URL to invoke the reminder end-point
		final String url = this.getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agreementId + REMINDERS_ENDPOINT;

		// Create header list
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.JSON.toString());
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response
		JSONObject responseJson = null;
		final JSONObject requestJson = FileUtils.getRequestJson(requestJsonFile);
		if (requestJson != null) {
			requestJson.replace("recipientParticipantIds", participantIds);
			responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.POST, headers,
					requestJson.toString());
		}
		return responseJson;
	}

	/**
	 * Returns workflow details.
	 *
	 * @param accessToken               Access token of the API user.
	 * @param agrId                     The workflow ID.
	 * @param includeNextParticipantSet Should the response have the next
	 *                                  participant set exclusively?
	 * @return JSON response containing the members of the agreement.
	 * @throws Exception
	 */
	public JSONObject workflowInfo(String accessToken, String workflowId) throws IOException {
		// URL to invoke the agreement end point.
		final String url = this.getBaseURL() + WORKFLOW_ENDPOINT + "/" + workflowId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

}
