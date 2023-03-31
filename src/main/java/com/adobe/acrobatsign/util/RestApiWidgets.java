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

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.adobe.acrobatsign.service.RestApiAgreements;

/**
 * Encapsulates calls to REST end points related to widgets, form data and related resources.
 */
public class RestApiWidgets {
  // End point components used by this class.
  private final static String WIDGETS_ENDPOINT = "/widgets";
  
  // Request JSON key names for uploaded file and uploaded form field document.
  private static final String FILE_INFOS = "fileInfos";

  /**
   * Creates a widget using the specified document. The document ID is added to the structure dynamically based on
   * the ID name specified by the caller, which also determines the format in which to specify the document ID.
   * 
   * @param accessToken Access token of the API user.
   * @param requestJsonFile Name of the file containing the JSON structure used as the input for this API call.
   * @param documentId Document ID of the document to be associated with the widget.
   * @param idName Name by which to refer to a list containing document IDs when adding it to the input JSON structure. Must be: 
   *          <ul>
   *          <li>DocumentIdentifierName.TRANSIENT_DOCUMENT_IDS</li>
   *          </ul>
   * @return JSON response containing the ID of the newly created widget.
   * @throws Exception
   * @see DocumentIdentifierName
   */
  @SuppressWarnings("unchecked")
  public static JSONObject createWidget(String accessToken, String requestJsonFile, String documentId, RestApiAgreements.DocumentIdentifierName idName) throws Exception {
    // URL to invoke the widgets end point.
    String url = RestApiUtils.getBaseURIForAPI(accessToken) + WIDGETS_ENDPOINT;

    // Create HTTP header list
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.JSON.toString());
    headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

    // Invoke API and get JSON response. The ID name determines what the input (request) JSON structure should contain.
    JSONObject responseJson = null;
    JSONObject widgetInfo = FileUtils.getRequestJson(requestJsonFile);
    if (widgetInfo != null) {
      
      // Check from the input parameter "idName" whether the file type to be used in creating a widget is a
      // transient document.
      // Based on the document type retrieved from above, set the corresponding item in the request JSON structure.
      if (idName == RestApiAgreements.DocumentIdentifierName.TRANSIENT_DOCUMENT_ID) {
        ArrayList<JSONObject> fileInfos = new ArrayList<JSONObject>();
        JSONObject fileInfo = new JSONObject();
        fileInfo.put(idName, documentId);
        fileInfos.add(fileInfo);
        widgetInfo.put(FILE_INFOS, fileInfos);
      }

      responseJson = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.POST, headers, widgetInfo.toString());
     }
    
    return responseJson;
  }

}
