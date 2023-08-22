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

import org.apache.commons.lang3.StringUtils;
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
import com.adobe.acrobatsign.model.LibraryDocument;
import com.adobe.acrobatsign.model.ReminderInfo;
import com.adobe.acrobatsign.model.ReminderInfo.StatusEnum;
import com.adobe.acrobatsign.model.ReminderParticipants;
import com.adobe.acrobatsign.model.RemindersResponse;
import com.adobe.acrobatsign.model.SearchRequestBody;
import com.adobe.acrobatsign.model.UserAgreement;
import com.adobe.acrobatsign.model.UserEvent;
import com.adobe.acrobatsign.model.UserEvents;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.FileUtils;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
			return actualName;
		}
	}

	// End point components used by this class.
	private static final String AGREEMENTS_ENDPOINT = "/agreements";
	private static final String TEMPLATES_ENDPOINT = "/libraryDocuments";

	private static final String WORKFLOW_ENDPOINT = "/workflows";

	private static final String GET_REMINDERS_ENDPOINT = "/reminders";

	private static final String CANCELAGREEMENT_ENDPOINT = "/reject";
	private static final String AUDIT_ENDPOINT = "/auditTrail";
	private static final String DOCUMENTS_ENDPOINT = "/documents";
	private static final String COMBINEDDOC_ENDPOINT = "/combinedDocument";
	private static final String MEMBERS_ENDPOINT = "/members";
	private static final String REMINDERS_ENDPOINT = "/reminders";
	private static final String SIGNING_URLS_ENDPOINT = "/signingUrls";
	private static final String SEARCH_AGREEMENTS = "/search";
	private static final String TRANSIENT_DOCUMENTS_ENDPOINT = "/transientDocuments";
	private static final String BASE_URL_API_V6 = "/api/rest/v6";
	private static final String GET_USERS = "/users";
	private static final String GET_LIBRARY_TEMPLATES = "/libraryDocuments";
	// private static final String GET_LIBRARY_TEMPLATES =
	// "/libraryDocuments?showHiddenLibraryDocuments=true";

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

	@Value(value = "${integration-key}")
	private String integrationKey;

	public void cancelAgreements(String accessToken, List<UserAgreement> agreementIdList, String userEmail)
			throws Exception {
		// URL to invoke the agreements end point.

		try {
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			final RestTemplate restTemplate = new RestTemplate();
			for (final UserAgreement agreement : agreementIdList) {

				final JSONObject cancelbody = new JSONObject();
				cancelbody.put("comment", "CANCEL");

				// Call function get Members

				final JSONObject MembersCancel = getAgreementMembers(accessToken, agreement.getId(), true);

				// JSON parsing

				final JSONArray ParticipantSetValues = (JSONArray) MembersCancel.get("nextParticipantSets");
				final JSONObject FirstPart = (JSONObject) ParticipantSetValues.get(0);
				final String partsetId = (String) FirstPart.get("id");

				final JSONArray ParticipantIDValues = (JSONArray) ((JSONObject) ParticipantSetValues.get(0))
						.get("memberInfos");
				final JSONObject firstinstance = (JSONObject) ParticipantIDValues.get(0);
				final String partId = (String) firstinstance.get("id");

				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/members/participantSets/")
						.append(partsetId).append("/participants/").append(partId).append(CANCELAGREEMENT_ENDPOINT);

				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}

				final HttpEntity<String> entity = new HttpEntity<>(cancelbody.toString(), restHeader);

				restTemplate.exchange(urlString.toString(), HttpMethod.PUT, entity, byte[].class);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void cancelReminders(String accessToken, List<UserAgreement> agreementIdList, String userEmail)
			throws Exception {
		// URL to invoke the agreements end point.
		try {
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			final RestTemplate restTemplate = new RestTemplate();
			for (final UserAgreement agreement : agreementIdList) {
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append(GET_REMINDERS_ENDPOINT);
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				final ResponseEntity<RemindersResponse> remindersResponse = restTemplate.exchange(urlString.toString(),
						HttpMethod.GET, entity, RemindersResponse.class);
				final List<ReminderInfo> reminderInfoList = remindersResponse.getBody().getReminderInfoList();
				for (final ReminderInfo reminderInfo : reminderInfoList) {
					final StringBuilder cancelReminderStr = new StringBuilder();
					final ReminderInfo updatedReminder = new ReminderInfo();
					updatedReminder.setStatus(StatusEnum.CANCELED);
					updatedReminder.setRecipientParticipantIds(reminderInfo.getRecipientParticipantIds());
					final HttpEntity<ReminderInfo> request = new HttpEntity<>(updatedReminder, restHeader);
					cancelReminderStr.append(endpointUrl).append("/").append(agreement.getId())
							.append(GET_REMINDERS_ENDPOINT).append("/").append(reminderInfo.getReminderId());
					restTemplate.exchange(cancelReminderStr.toString(), HttpMethod.PUT, request, ReminderInfo.class);
				}
			}
		} catch (final Exception e) {
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
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			final RestTemplate restTemplate = new RestTemplate();

			for (final UserAgreement agreement : agreementIdList) {
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/documents");
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				restTemplate.exchange(urlString.toString(), HttpMethod.DELETE, entity, byte[].class);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public String downloadAgreements(String accessToken, List<UserAgreement> agreementIdList, String userEmail,
			HttpServletResponse response) {
		// URL to invoke the agreements end point.
		final RestTemplate restTemplate = new RestTemplate();
		String agreementName = null;
		// Create header list.
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			zos = new ZipOutputStream(response.getOutputStream());
			for (final UserAgreement agreement : agreementIdList) {
				agreementName = agreement.getId() + "----" + agreement.getName();
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/combinedDocument");

				final StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(downloadPath).append(agreement.getUserEmail());

				final File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}
				// ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != agreement.getUserEmail()) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}

				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				ResponseEntity<byte[]> resource = null;
				boolean flag = false;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (final Exception e) {
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
					final ZipEntry entry = new ZipEntry(fileName + ".pdf");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".pdf"), resource.getBody());
				}
			}
			zos.close();
		} catch (final Exception e) {
			System.out.println("Agreement name --" + agreementName);
			e.printStackTrace();
		}
		return "SUCCESS";
	}

	public ZipOutputStream downloadFormFields(String accessToken, List<UserAgreement> agreementIdList, String userEmail,
			HttpServletResponse response) {
		// URL to invoke the agreements end point.
		final RestTemplate restTemplate = new RestTemplate();
		String agreementName = null;
		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			zos = new ZipOutputStream(response.getOutputStream());
			// Invoke API and get JSON response.
			for (final UserAgreement agreement : agreementIdList) {
				agreementName = agreement.getId() + "----" + agreement.getName();
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/formData");

				final StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(downloadPath).append(agreement.getUserEmail());

				final File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}

				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}

				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				boolean flag = false;
				ResponseEntity<byte[]> resource = null;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (final Exception e) {
					System.out.println("Issue in Agreement name --" + agreementName);
					flag = true;
				}
				if (!flag) {
					String fileName = agreement.getId();
					if (agreement.getName().matches(REGEX_PATTERN)) {
						fileName = fileName + "_" + agreement.getName();
					}
					final ZipEntry entry = new ZipEntry(fileName + ".csv");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".csv"), resource.getBody());
				}
			}
			zos.close();
		} catch (final Exception e) {
			System.out.println("Agreement name --" + agreementName);
			e.printStackTrace();
		}
		return zos;
	}

	public ZipOutputStream downloadTemplateFormFields(String accessToken, List<LibraryDocument> libraryTemplateList,
			String userEmail, HttpServletResponse response) {
		// URL to invoke the agreements end point.
		final RestTemplate restTemplate = new RestTemplate();
		String templateName = null;
		// Create header list.
		/*
		 * final Map<String, String> headers = new HashMap<>();
		 * headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(),
		 * accessToken);
		 * headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(),
		 * "application/json");
		 */
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = getBaseURL() + TEMPLATES_ENDPOINT;
			zos = new ZipOutputStream(response.getOutputStream());
			// Invoke API and get JSON response.
			for (final LibraryDocument libraryDocument : libraryTemplateList) {
				templateName = libraryDocument.getId() + "----" + libraryDocument.getName();
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(libraryDocument.getId()).append("/formData");

				final StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(downloadPath).append(libraryDocument.getOwnerEmail());

				final File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				restHeader.add(RestApiUtils.HttpHeaderField.ACCEPT.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + libraryDocument.getOwnerEmail());
				}
				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				boolean flag = false;
				ResponseEntity<byte[]> resource = null;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (final Exception e) {
					System.out.println("Issue in Template name --" + templateName);
					flag = true;
				}
				if (!flag) {
					String fileName = libraryDocument.getId();
					if (libraryDocument.getName().matches(REGEX_PATTERN)) {
						fileName = fileName + "_" + libraryDocument.getName();
					}
					final ZipEntry entry = new ZipEntry(fileName + ".csv");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".csv"), resource.getBody());
				}
			}
			zos.close();
		} catch (final Exception e) {
			System.out.println("Template name --" + templateName);
			e.printStackTrace();
		}
		return zos;
	}

	public String downloadTemplates(String accessToken, List<LibraryDocument> seletedTemplateList, String userEmail,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		// URL to invoke the agreements end point.
		final RestTemplate restTemplate = new RestTemplate();
		String templateName = null;
		// Create header list.
		ZipOutputStream zos = null;
		try {
			final String endpointUrl = getBaseURL() + GET_LIBRARY_TEMPLATES;
			zos = new ZipOutputStream(response.getOutputStream());
			for (final LibraryDocument libraryDocument : seletedTemplateList) {
				templateName = libraryDocument.getId() + "----" + libraryDocument.getName();
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(libraryDocument.getId()).append("/combinedDocument");

				final StringBuilder directoryPath = new StringBuilder();
				directoryPath.append(downloadPath).append(libraryDocument.getOwnerEmail());

				final File directory = new File(directoryPath.toString());
				if (!directory.exists()) {
					directory.mkdir();
				}
				// ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				if (null != userEmail) {
					restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + libraryDocument.getOwnerEmail());
				}

				final HttpEntity<String> entity = new HttpEntity<>("body", restHeader);
				ResponseEntity<byte[]> resource = null;
				boolean flag = false;
				try {
					resource = restTemplate.exchange(urlString.toString(), HttpMethod.GET, entity, byte[].class);
					flag = false;
				} catch (final Exception e) {
					System.out.println("Issue in Template name --" + templateName);
					flag = true;
				}
				// byte[] resource = (byte[])
				// RestApiUtils.makeApiCall(url,RestApiUtils.HttpRequestMethod.GET, headers);
				if (!flag) {
					String fileName = libraryDocument.getId();
					if (libraryDocument.getName().matches(REGEX_PATTERN)) {
						fileName = fileName + "_" + libraryDocument.getName();
					}
					final ZipEntry entry = new ZipEntry(fileName + ".pdf");
					entry.setSize(resource.getBody().length);
					zos.putNextEntry(entry);
					zos.write(resource.getBody());
					zos.closeEntry();

					Files.write(Paths.get(directory + "/" + fileName + ".pdf"), resource.getBody());
				}
			}
			zos.close();
		} catch (final Exception e) {
			System.out.println("Agreement name --" + templateName);
			e.printStackTrace();
		}
		return "SUCCESS";
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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + AUDIT_ENDPOINT;

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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + COMBINEDDOC_ENDPOINT;

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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + DOCUMENTS_ENDPOINT;

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
	public JSONObject getAgreementInfo(String accessToken, String agrId, String email) throws IOException {
		// URL to invoke the agreement end point.
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		if (null != email) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + email);
		}

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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + MEMBERS_ENDPOINT
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
			List<String> status, Integer size, String userGroup) throws Exception {
		// URL to invoke the agreements end point.
		final String endpointUrl = getBaseURL() + SEARCH_AGREEMENTS;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.ACCEPT.toString(), "application/json");
		headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		if (!StringUtils.isEmpty(userEmail)) {
			headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + userEmail);
		}

		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		final Date formatStartDate = formatter1.parse(startDate);
		final String startDateStr = formatter.format(formatStartDate);

		final Date formatBeforeDate = formatter1.parse(beforeDate);
		final String beforeDateStr = formatter.format(formatBeforeDate);

		final DateRange dateRange = new DateRange();
		final DateRangeFilter range = new DateRangeFilter();

		dateRange.setGt(startDateStr);
		dateRange.setLt(beforeDateStr);

		range.setRange(dateRange);

		final JSONObject currAgreementList = null;
		final SearchRequestBody searchRequestBody = new SearchRequestBody();
		final List<String> scope = new ArrayList<>();
		final List<String> group = new ArrayList<>();
		final AgreementAssetsCriteria agreementAssetsCriteria = new AgreementAssetsCriteria();

		scope.add("AGREEMENT_ASSETS");
		if (!StringUtils.isEmpty(userGroup)) {
			group.add(userGroup);
			agreementAssetsCriteria.setStatus(status);
			if (!userGroup.equalsIgnoreCase("ABC")) {
				agreementAssetsCriteria.setGroupId(group);
			}
		}

		agreementAssetsCriteria.setType(type);
		agreementAssetsCriteria.setModifiedDate(range);
		agreementAssetsCriteria.setRole(role);
		agreementAssetsCriteria.setVisibility(visibility);
		agreementAssetsCriteria.setStartIndex(size);
		searchRequestBody.setScope(scope);

		searchRequestBody.setAgreementAssetsCriteria(agreementAssetsCriteria);

		final ObjectWriter searchRequestJSON = new ObjectMapper().writer().withDefaultPrettyPrinter();
		final String json = searchRequestJSON.writeValueAsString(searchRequestBody);
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
		return baseUrl + BASE_URL_API_V6;
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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + DOCUMENTS_ENDPOINT + "/" + docId;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get bytes of file.
		byte[] responseBytes = null;
		responseBytes = (byte[]) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseBytes;
	}

	public String getIntegrationKey() {
		return integrationKey;
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
		final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
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
			if (cursor != null && !cursor.trim().isEmpty()) {
				url = endpointUrl + "?cursor=" + cursor;
			}
		} while (cursor != null);

		return agreementList;
	}

	public List<String> getReminders(String accessToken, List<UserAgreement> agreementIdList, String userEmail)
			throws Exception {

		List<String> evts = new ArrayList<>();
		List<String> ignoredIDS = new ArrayList<>();
		UserEvents userEventList = new UserEvents();
		StringBuffer key = new StringBuffer();
		// String lastID = "";
		// String lastType = "";

		try {
			String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			for (final UserAgreement agreement : agreementIdList) {

				accessToken = Constants.BEARER + getIntegrationKey();
				endpointUrl = endpointUrl + "/" + agreement.getId() + Constants.GET_EVENTS;
				final Map<String, String> headers = new HashMap<>();
				headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JSONObject userEventInfoList = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
						RestApiUtils.HttpRequestMethod.GET, headers);

				if (userEventInfoList == null) {
					ignoredIDS.add(agreement.getId());
					endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
					continue;
				}

				userEventList = objectMapper.readValue(userEventInfoList.toJSONString(), UserEvents.class);
				endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
				List<UserEvent> allEvents = userEventList.getEvents();

				for (int loop = 0; loop < allEvents.size(); loop++) {
					if (allEvents.get(loop).getType().equalsIgnoreCase("REMINDER_SENT")) {

						key.append(agreement.getId());
						key.append("|");
						key.append(allEvents.get(loop).getDate());
						List<ReminderParticipants> reminderEvents = allEvents.get(loop).getReminderParticipants();

						for (int loop1 = 0; loop1 < reminderEvents.size(); loop1++) {
							key.append(", ");
							key.append(reminderEvents.get(loop1).getName());
							key.append("(");
							key.append(reminderEvents.get(loop1).getEmail());
							key.append(")");
						}
						/*
						 * if (lastID.equalsIgnoreCase(agreement.getId()) &&
						 * (!lastType.equalsIgnoreCase("REMINDER_SENT"))) { evts.remove(evts.size() -1);
						 * }
						 */
						evts.add(key.toString());
						key = new StringBuffer();
					}
					/*
					 * else { key.append(agreement.getId()); key.append("|");
					 * key.append(allEvents.get(loop).getDate()); key.append("|");
					 * key.append("NO DATA");
					 *
					 * if (!lastID.equalsIgnoreCase(agreement.getId())) { evts.add(key.toString());
					 * } key = new StringBuffer(); } lastID = agreement.getId(); lastType =
					 * allEvents.get(loop).getType();
					 */
				}
			}
			// System.out.println("ID IGNORED -------> "+ ignoredIDS.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return evts;
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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agrId + SIGNING_URLS_ENDPOINT;

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJson = null;
		responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		return responseJson;
	}

	public JSONObject getUsers(String accessToken) throws IOException {
		// TODO Auto-generated method stub

		final String endpointUrl = getBaseURL() + GET_USERS;
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		final JSONObject userDetails = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
				RestApiUtils.HttpRequestMethod.GET, headers);
		return userDetails;
	}

	public JSONObject getUserTemplate(String accessToken, String userEmail, String nextCursor) throws IOException {
		String endpointUrl = null;
		if (null != nextCursor) {
			endpointUrl = getBaseURL() + GET_LIBRARY_TEMPLATES + "/?cursor=" + nextCursor;
		} else {
			endpointUrl = getBaseURL() + GET_LIBRARY_TEMPLATES;
		}

		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
		headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + userEmail);

		final JSONObject userTemplates = (JSONObject) RestApiUtils.makeApiCall(endpointUrl,
				RestApiUtils.HttpRequestMethod.GET, headers);
		return userTemplates;
	}

	public void hideAgreements(String accessToken, List<UserAgreement> agreementIdList) {
		// URL to invoke the agreements end point.
		try {
			final String endpointUrl = getBaseURL() + AGREEMENTS_ENDPOINT;
			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
			// Invoke API and get JSON response.
			final JSONObject hideJson = new JSONObject();
			hideJson.put("visibility", "HIDE");
			final String cursor = null;
			final RestTemplate restTemplate = new RestTemplate();
			for (final UserAgreement agreement : agreementIdList) {
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(agreement.getId()).append("/me/visibility");

				if (null != agreement.getUserEmail()) {
					headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + agreement.getUserEmail());
				}
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + agreement.getUserEmail());

				final HttpEntity<String> entity = new HttpEntity<>(hideJson.toString(), restHeader);

				restTemplate.exchange(urlString.toString(), HttpMethod.PUT, entity, byte[].class);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public void hideTemplates(String accessToken, List<LibraryDocument> seletedList) {
		try {
			final String endpointUrl = getBaseURL() + TEMPLATES_ENDPOINT;
			// Create header list.
			final Map<String, String> headers = new HashMap<>();
			headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
			headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
			// Invoke API and get JSON response.
			final JSONObject hideJson = new JSONObject();
			hideJson.put("visibility", "HIDE");
			final String cursor = null;
			final RestTemplate restTemplate = new RestTemplate();
			for (final LibraryDocument template : seletedList) {
				final StringBuilder urlString = new StringBuilder();
				urlString.append(endpointUrl).append("/").append(template.getId()).append("/me/visibility");

				if (null != template.getOwnerEmail()) {
					headers.put(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(),
							"email:" + template.getOwnerEmail());
				}
				final HttpHeaders restHeader = new HttpHeaders();
				restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);
				restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
				restHeader.add(RestApiUtils.HttpHeaderField.USER_EMAIL.toString(), "email:" + template.getOwnerEmail());

				final HttpEntity<String> entity = new HttpEntity<>(hideJson.toString(), restHeader);

				restTemplate.exchange(urlString.toString(), HttpMethod.PUT, entity, byte[].class);
			}
		} catch (final Exception e) {
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
		final String url = getBaseURL() + TRANSIENT_DOCUMENTS_ENDPOINT;
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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT;

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
			if (idName == DocumentIdentifierName.LIBRARY_DOCUMENT_ID
					|| idName == DocumentIdentifierName.TRANSIENT_DOCUMENT_ID) {

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
		final String url = getBaseURL() + AGREEMENTS_ENDPOINT + "/" + agreementId + REMINDERS_ENDPOINT;

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
