/*************************************************************************
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2018 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE: Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it. If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/

package com.adobe.acrobatsign.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods to make file-related calls.
 *
 * <p>
 * <b>IMPORTANT</b>: Check that the output location in the field
 * <code>OUTPUT_PATH</code> below is suitable.
 * </p>
 */
public class FileUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	/** The Constant FILE_SEP. */
	// Platform-dependent file-path separator.
	public static final String FILE_SEP = System.getProperty("file.separator");

	/** The Constant OUTPUT_PATH. */
	// Location where output files are saved. Default is a sub-folder in the system
	// temp folder.
	public static final String OUTPUT_PATH = getDefaultOutputPath();

	/** The Constant REQUEST_PATH. */
	// The package path from which to read any input files.
	public static final String REQUEST_PATH = "adobesign/api/rest/sample/requests/";

	/** The Constant AUDIT_FILES_OUTPUT_PATH. */
	public static final String AUDIT_FILES_OUTPUT_PATH = OUTPUT_PATH + "auditTrails" + FILE_SEP;

	/** The Constant AGREEMENT_DOCS_OUTPUT_PATH. */
	public static final String AGREEMENT_DOCS_OUTPUT_PATH = OUTPUT_PATH + "agreementDocuments" + FILE_SEP;

	/** The Constant AGREEMENT_COMBINED_DOC_OUTPUT_PATH. */
	public static final String AGREEMENT_COMBINED_DOC_OUTPUT_PATH = OUTPUT_PATH + "combinedDocument" + FILE_SEP;

	/**
	 * Check if the specified directory exists. If not, create it.
	 *
	 * @param dirPath The directory.
	 * @return True if success, false otherwise.
	 */
	private static boolean ensureDirectoryExists(String dirPath) {
		final File path = new File(dirPath);
		if (path.exists() && path.isDirectory()) {
			return true;
		}
		return path.mkdirs();
	}

	/**
	 * Returns the root path in which to store output files. Includes a trailing
	 * path separator.
	 *
	 * @return the default output path
	 */
	private static String getDefaultOutputPath() {
		String outputPath = System.getProperty("java.io.tmpdir");
		if ((outputPath == null) || outputPath.isEmpty()) {
			outputPath = "." + FILE_SEP; // current directory
		} else if (!outputPath.endsWith(FILE_SEP)) {
			outputPath += FILE_SEP;
		}

		// Add a suitable sub-directory.
		outputPath = outputPath + "AdobeSignRestSamplesOutput" + FILE_SEP;

		return outputPath;
	}

	/**
	 * Gets the jsonfrom object.
	 *
	 * @param obj the obj
	 * @return the jsonfrom object
	 */
	public static String getJsonfromObject(Object obj) {
		final ObjectMapper objectMapper = new ObjectMapper();
		String str = null;
		try {
			str = objectMapper.writeValueAsString(obj);
		} catch (final JsonProcessingException e) {
			LOGGER.error(e.getMessage());
		}
		return str;
	}

	/**
	 * Parses the specified file to a JSON object. The file must contain a JSON
	 * structure for use as the request body (input) in the REST API calls.
	 *
	 * @param fileName Name of the file containing the JSON structure.
	 * @return JSONObject obtained from the file's contents.
	 * @throws IOException    Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static JSONObject getRequestJson(String fileName) throws IOException, ParseException {
		JSONObject json = null;
		// Create the pointer to the file containing sample JSON relative to
		// FileUtils.java
		final String name = REQUEST_PATH + fileName;
		try (InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(name))) {
			final JSONParser parser = new JSONParser();

			json = (JSONObject) parser.parse(reader);
		} catch (final ParseException e) {
			LOGGER.error(RestError.GENERAL_PARSING_ERROR.errMessage);
			throw e;
		}
		return json;
	}

	/**
	 * Saves bytes to the specified location with the specified name.
	 *
	 * @param fileData the file data
	 * @param dirPath  Folder location where the file is to be saved; must end in a
	 *                 path separator.
	 * @param fileName Name with which the file is to be saved.
	 * @return True if successful, false otherwise.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean saveToFile(byte[] fileData, String dirPath, String fileName) throws IOException {
		// Attempt to save only if data is not null and output directory exists
		if ((fileData != null) && ensureDirectoryExists(dirPath)) {
			try (BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dirPath + fileName))) {
				outStream.write(fileData, 0, fileData.length);
				return true;
			} catch (final IOException e) {
				LOGGER.error(RestError.INPUT_OUTPUT_EXCEPTION.errMessage);
				throw e;
			}
		}

		// One of the inputs is probably invalid.
		return false;
	}

	private FileUtils() {
		LOGGER.info("FileUtils Private Constructor");
	}
}
