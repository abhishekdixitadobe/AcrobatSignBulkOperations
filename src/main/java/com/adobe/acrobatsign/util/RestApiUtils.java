package com.adobe.acrobatsign.util;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods to make the actual REST call. The main methods of interest
 * for callers are the makeApiCall overloads. Most others are helpers to perform
 * the low-level operations involved in making the call, such as establishing
 * network connections, etc.
 */
public class RestApiUtils {
	/**
	 * Representation of common HTTP header fields relevant for the REST API.
	 */
	public enum HttpHeaderField {
		CONTENT_TYPE("Content-Type"), AUTHORIZATION("Authorization"), FILE_NAME("File-Name"), MIME_TYPE("Mime-Type"),
		USER_EMAIL("x-api-user"), ACCEPT("ACCEPT"), AGREEMENT_ID("agreementId");

		private final String fieldName;

		HttpHeaderField(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String toString() {
			return this.fieldName;
		}
	}

	/**
	 * Representation of the HTTP Request methods used by this class.
	 */
	public enum HttpRequestMethod {
		GET, POST, PUT, DELETE;
	}

	/**
	 * Representation of common mime types used by this class.
	 */
	public enum MimeType {
		JSON("application/json;charset=UTF-8"), PDF("application/pdf"), PLAIN_TEXT("text/plain"),
		APPLICATIONJSON("application/json"), FORM("application/x-www-form-urlencoded");

		private final String typeString;

		MimeType(String typeString) {
			this.typeString = typeString;
		}

		@Override
		public String toString() {
			return this.typeString;
		}
	}

	private static final String UTF_8 = "UTF-8";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestApiUtils.class);

	// REST API base URLs
	public static final String OAUTH_BASE_URL = "https://secure.echosign.com/oauth";

	/**
	 * Provide services base URI here. This will be used as base URI for subsequent
	 * REST API calls. If base URI is not provided then an REST API call will be
	 * made to retrieve it.
	 */
	public static final String SERVICES_BASE_URL = "";

	/**
	 * Prepares a multi-part HTTP request (containing a file to upload) for calling
	 * the specified API URL. Does not actually make the request.
	 *
	 * @param apiUrl       URL for the API request.
	 * @param headers      HTTP header fields and values required for the API
	 *                     request.
	 * @param fileMetaData Meta-data associated with the file, to be used as parts
	 *                     in the multi-part request body.
	 * @param fileToUpload Reference to the file being uploaded.
	 * @return The URL connection object if successful, not yet connected.
	 * @throws IOException
	 */
	private static HttpsURLConnection createMultiPartRequest(String apiUrl, Map<String, String> headers,
			Map<String, String> fileMetaData, File fileToUpload) throws IOException {
		// Create a connection. Allow any exceptions this throws to pass up to the
		// caller.
		// The request method is always POST.
		final HttpsURLConnection connection = createRequest(apiUrl, HttpRequestMethod.POST, headers, null);

		if (null == connection) {
			return connection;
		}

		// Output stream of the connection.
		OutputStream outputStream = null;

		try {

			// Create an encapsulation of the request body with multi-part content.
			final MultiPartRequestBody multiPartBody = new MultiPartRequestBody();

			// Set the Content-Type header.
			final String contentType = "multipart/form-data; boundary=" + multiPartBody.getBoundary();
			connection.setRequestProperty(HttpHeaderField.CONTENT_TYPE.toString(), contentType);

			// Give the multi-part body a stream to write to.
			outputStream = connection.getOutputStream();
			multiPartBody.setOutputStream(outputStream);

			// The POST request to transient documents requires three parts to be provided.
			// Part 1 - File-Name: Name given to the file being uploaded.
			final String fileNameHeader = HttpHeaderField.FILE_NAME.toString();
			multiPartBody.addMetaPart(fileNameHeader, fileMetaData.get(fileNameHeader));

			// Part 2 - Mime-Type: The mime type of the file being uploaded.
			final String mimeTypeHeader = HttpHeaderField.MIME_TYPE.toString();
			multiPartBody.addMetaPart(mimeTypeHeader, fileMetaData.get(mimeTypeHeader));

			// Part 3 - File: Content stream of the file.
			multiPartBody.addFilePart(fileMetaData.get(fileNameHeader), fileMetaData.get(mimeTypeHeader), fileToUpload);

			// Mark the multi-part request body as completed.
			multiPartBody.endBody();
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(RestError.UNSUPPORTED_ENCODING.errMessage);
			throw e;
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return connection;
	}

	/**
	 * Prepares an HTTP request for calling the specified API URL. Does not actually
	 * make the request.
	 *
	 * @param apiUrl  URL for the API request.
	 * @param method  HTTP request method for the API request.
	 * @param headers HTTP header fields and values required for the API request.
	 * @param body    Optional HTTP request body. May be null.
	 * @return The URL connection object if successful, not yet connected.
	 * @throws IOException
	 */
	private static HttpsURLConnection createRequest(String apiUrl, HttpRequestMethod method,
			Map<String, String> headers, String body) throws IOException {
		// Initiate connection to URL
		HttpsURLConnection conn = null;
		boolean allGood = false;

		try {
			conn = openConnection(apiUrl);
			if (conn != null) {
				// Set the HTTP Request method.
				conn.setRequestMethod(method.toString());

				// Add header field/value pairs to the API request.
				for (final Entry<String, String> map : headers.entrySet()) {
					conn.setRequestProperty(map.getKey(), map.getValue());
				}

				// Set body for the API call if one is specified
				if (body != null) {
					setRequestBody(conn, body);
				}

				// If we reached here no exceptions were thrown.
				allGood = true;
			}
		} catch (final MalformedURLException e) {
			LOGGER.error(RestError.MALFORMED_URL.errMessage);
			throw e;
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		} finally {
			// Disconnect in the face of any exception. We could have simply caught
			// Exception and rethrown that, but the
			// method does not declare that it throws general Exceptions (since it in fact
			// does not explicitly do so).
			if (!allGood && (conn != null)) {
				conn.disconnect();
			}
		}
		return conn;
	}

	/**
	 * Executes the API call associated with the specified connection object.
	 *
	 * @param conn HttpsURLConnection object associated with the request.
	 * @return Either a JSON object or a byte array depending on the request made.
	 *         Null in case of an error.
	 * @throws IOException
	 */
	private static Object executeRequest(HttpsURLConnection conn) throws IOException {
		Object responseObj = null; // return as an Object since actual type is not yet known
		int status = 0;

		// Open the URL connection, and await a response.
		try {
			conn.connect();
			status = conn.getResponseCode();

			// Check for successful API invocation
			if ((status >= 200) && (status <= 299)) {
				// Success. Get the response body using the connection's regular input stream.
				responseObj = getSuccessfulResponseObj(conn);
			} else {
				// Error. Don't set responseObj. Instead show on the console what is retrieved
				// using the connection's error stream.
				getFailedResponseObj(conn, status);
			}
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		}
		return responseObj;
	}

	/**
	 * This method will return the base uri to access other REST APIs. In case other
	 * APIs are accessed from a different base URI, it will be considered an invalid
	 * request
	 *
	 * @param accessToken Access token of the API user.
	 * @throws IOException
	 * @throws Exception
	 */
	public static String getBaseURIForAPI(String accessToken) throws IOException {

		if (!SERVICES_BASE_URL.isEmpty()) {
			return SERVICES_BASE_URL;
		}

		// URL to base_uri REST endpoint.
		final String url = "https://secure.in1.echosign.com/api/rest/v6/baseUris";

		// Create header list.
		final Map<String, String> headers = new HashMap<>();
		headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

		// Invoke API and get JSON response.
		JSONObject responseJSON = null;
		responseJSON = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);

		String apiAccessPoint = null;
		if (null != responseJSON) {
			// This will be base URI for other REST API calls.
			apiAccessPoint = (String) responseJSON.get("apiAccessPoint") + "api/rest/v6";
		}

		// Provide this base URI value to SERVICES_BASE_URL static variable declared
		// above in this class.
		// This will be used as base URI for subsequent REST API calls.
		LOGGER.info("Base URI : " + apiAccessPoint);

		return apiAccessPoint;

	}

	/**
	 * Given an attempted failed connection, reads the body of the response as a
	 * JSON object using the connection's error stream, and writes it to console.
	 *
	 * @param conn HttpsURLConnection object associated with the request. Connection
	 *             must already have failed.
	 * @throws IOException
	 */
	private static void getFailedResponseObj(HttpsURLConnection conn, int errorCode) throws IOException {
		InputStream connStream = null;
		try {
			// Get the body of the response as an array of bytes using the connection's
			// error stream.
			connStream = conn.getErrorStream();
			if (connStream != null) {
				final String responseContentType = conn.getContentType();
				final byte[] responseBytes = getResponseBytes(conn, connStream);

				// Write out the error details to the console, but don't set response. We want
				// to return null to indicate an error.
				if (responseContentType.equals(MimeType.JSON.toString())) {
					final JSONObject jobj = (JSONObject) JSONValue.parse(new String(responseBytes, UTF_8));
					LOGGER.error(RestError.GENERAL_ERROR.errMessage + ", HTTP response code: " + errorCode);
					LOGGER.info("Message: " + jobj.get("message"));
					LOGGER.info("Code: " + jobj.get("code"));
				}
			}
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(RestError.UNSUPPORTED_ENCODING.errMessage);
			throw e;
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		} finally {
			if (connStream != null) {
				connStream.close();
			}
		}
	}

	/**
	 * Get the response body from the API call as a byte array.
	 *
	 * @param conn       The HTTP request URL connection object.
	 * @param connStream The connection stream to use.
	 * @return The response body.
	 * @throws IOException
	 */
	private static byte[] getResponseBytes(HttpsURLConnection conn, InputStream connStream) throws IOException {
		byte[] responseBytes = new byte[4096];
		final InputStream inputStream = new BufferedInputStream(connStream);
		int totalBytesRead = 0;

		try {
			int bytesRead = inputStream.read(responseBytes);

			// Read all the bytes of the connection stream. Since it may happen that the
			// content length is unknown we must keep reallocating our
			// buffer as we encounter new data. Note that this may not be the most efficient
			// way to do this.
			while (bytesRead >= 0) {
				totalBytesRead += bytesRead;

				// Allocate a larger byte array for the next read operation.
				responseBytes = Arrays.copyOf(responseBytes, totalBytesRead + 4096);

				// Read into the re-allocated buffer.
				bytesRead = inputStream.read(responseBytes, totalBytesRead, responseBytes.length - totalBytesRead);
			}

			// Final reallocation to return an array that is precisely the length of the
			// data that was read in.
			responseBytes = Arrays.copyOfRange(responseBytes, 0, totalBytesRead);
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		}

		return responseBytes;
	}

	/**
	 * Given an established successful connection, returns the body of the response
	 * as either a JSON object or a byte array by reading the connection's regular
	 * input stream.
	 *
	 * @param conn HttpsURLConnection object associated with the request. Connection
	 *             must already have been established.
	 * @return Body of the HTTP response, as either a JSON object or byte array.
	 * @throws IOException
	 */
	private static Object getSuccessfulResponseObj(HttpsURLConnection conn) throws IOException {
		Object responseObj = null;
		InputStream connStream = null;
		try {
			connStream = conn.getInputStream();
			if (connStream != null) {
				// Read the bytes of the response body.
				final String responseContentType = conn.getContentType();
				final byte[] responseBytes = getResponseBytes(conn, connStream);

				// Set responseObj based on the content type of the response received. Note that
				// even though the type of responseObj is
				// Object, type-casting to JSONObject helps track class cast exceptions if they
				// occur.
				if (responseContentType.equals(MimeType.JSON.toString())) {
					responseObj = JSONValue.parse(new String(responseBytes, UTF_8));
				} else {
					responseObj = responseBytes;
				}
			}
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(RestError.UNSUPPORTED_ENCODING.errMessage);
			throw e;
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		} finally {
			if (connStream != null) {
				connStream.close();
			}
		}
		return responseObj;
	}

	/**
	 * Invokes the REST API at the specified end point using the specified method
	 * and headers.
	 *
	 * @param apiUrl  URL for the API request.
	 * @param method  HTTP request method for the API request.
	 * @param headers HTTP header fields and values required for the API request.
	 * @return The body of the HTTP response returned by the API call.
	 * @throws IOException
	 */
	public static Object makeApiCall(String apiUrl, HttpRequestMethod method, Map<String, String> headers)
			throws IOException {
		return makeApiCall(apiUrl, method, headers, null);
	}

	/**
	 * Invokes the REST API at the specified end point using the specified method
	 * and headers and an optional body.
	 *
	 * @param apiUrl  URL for the API request.
	 * @param method  HTTP request method for the API request.
	 * @param headers HTTP header fields and values required for the API request.
	 * @param body    Optional HTTP request body for the API request. May be null.
	 * @return The body of the HTTP response returned by the API call.
	 * @throws IOException
	 */
	public static Object makeApiCall(String apiUrl, HttpRequestMethod method, Map<String, String> headers, String body)
			throws IOException {
		Object response = null;

		// Open an HTTPS connection in preparation for the call.
		final HttpsURLConnection conn = createRequest(apiUrl, method, headers, body);
		if (conn != null) {
			try {
				// Make the call over the opened connection.
				response = executeRequest(conn);
			} finally {
				// Irrespective of success or failure, close the connection.
				conn.disconnect();
			}
		}
		return response;
	}

	/**
	 * Invokes any REST API end point that accepts a file as a payload. The file is
	 * sent in a multi-part request body using the specified headers.
	 *
	 * @param apiUrl       URL for the API request.
	 * @param headers      HTTP header fields and values required for the API
	 *                     request.
	 * @param fileMetaData Meta-data associated with the file, to be used as parts
	 *                     in the multi-part request body.
	 * @param fileToUpload reference to the file being uploaded.
	 * @return The body of the HTTP response returned by the API call.
	 * @throws IOException
	 */
	public static Object makeApiCall(String apiUrl, Map<String, String> headers, Map<String, String> fileMetaData,
			File fileToUpload) throws IOException {
		Object response = null;

		// Open an HTTPS connection in preparation for the call.
		final HttpsURLConnection conn = createMultiPartRequest(apiUrl, headers, fileMetaData, fileToUpload);
		if (conn != null) {
			try {
				// Make the call over the opened connection.
				response = executeRequest(conn);
			} finally {
				// Irrespective of success or failure, close the connection.
				conn.disconnect();
			}
		}
		return response;
	}

	/**
	 * Set up an HTTPS connection to the specified URL.
	 *
	 * @param apiUrl URL for the API request.
	 * @return True on success, false otherwise.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static HttpsURLConnection openConnection(String apiUrl) throws MalformedURLException, IOException {
		HttpsURLConnection conn = null;
		try {
			final URL url = new URL(apiUrl);
			conn = (HttpsURLConnection) url.openConnection();
		} catch (final MalformedURLException e) {
			LOGGER.error(RestError.MALFORMED_URL.errMessage);
			throw e;
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		}

		if (conn != null) {
			// This connection is used for both input and output.
			conn.setDoInput(true);
			conn.setDoOutput(true);
		}
		return conn;
	}

	/**
	 * Converts the specified string to bytes using UTF-8 encoding and sets the
	 * result as the body of the HTTPS request going out.
	 *
	 * @param conn The HTTP request URL connection object.
	 * @param body String to set as the body for the HTTP request.
	 * @throws IOException
	 */
	private static void setRequestBody(HttpsURLConnection conn, String body) throws IOException {
		DataOutputStream outStream = null;
		try {
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(body.getBytes(UTF_8));
		} catch (final IOException e) {
			LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
			throw e;
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
	}
}
