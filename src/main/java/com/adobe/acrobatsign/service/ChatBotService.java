package com.adobe.acrobatsign.service;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.UserInfo;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class ChatBotService {

	@Value(value = "${openai-service.gpt-model}")
	private String gptModel;

	@Value(value = "${openai-service.urls.base-url}")
	private String chatGPTURL;

	@Value(value = "${openai-service.api-key}")
	private String openAPIKey;

	@Autowired
	private ObjectMapper jsonMapper;

	@Autowired
	AdobeSignService adobeSignService;

	public String chatWithGpt3(String content) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), "Bearer "
				+ "eyJhbGciOiJSUzI1NiIsIng1dSI6Imltc19uYTEtc3RnMS1rZXktYXQtMS5jZXIiLCJraWQiOiJpbXNfbmExLXN0ZzEta2V5LWF0LTEiLCJpdHQiOiJhdCJ9.eyJpZCI6IjE2OTU0MDQ0OTI2NzNfN2JjZmY0NDctZThmZC00NDcyLTgwNDEtMDQ5M2JmOWNkNTZkX2V3MSIsInR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJjbGllbnRfaWQiOiJzY29lLWhhY2thdGhvbi1hcHAiLCJ1c2VyX2lkIjoic2NvZS1oYWNrYXRob24tYXBwQEFkb2JlSUQiLCJhcyI6Imltcy1uYTEtc3RnMSIsImFhX2lkIjoic2NvZS1oYWNrYXRob24tYXBwQEFkb2JlSUQiLCJjdHAiOjAsInBhYyI6InNjb2UtaGFja2F0aG9uLWFwcF9zdGciLCJydGlkIjoiMTY5NTQwNDQ5MjY3M181YzM1MWUyZi05MzQzLTRhYTQtYWJkNy1mYWM4YmZhNTMzNWFfZXcxIiwibW9pIjoiYWZiMjE1NDgiLCJydGVhIjoiMTY5NjYxNDA5MjY3MyIsImV4cGlyZXNfaW4iOiI4NjQwMDAwMCIsImNyZWF0ZWRfYXQiOiIxNjk1NDA0NDkyNjczIiwic2NvcGUiOiJzeXN0ZW0ifQ.AiPGZZD43PkPEUFEx6FuaaACnS97oa0k4aUGthaqvmAUIQ6HTWgaVo-tn7_cbcB7CFutm7fsgJY7gLsV-vtS2MoE3kVXkxc5SbWfxjgNykXhdq7zGOPA8kWg5NkaQyshBBYjUzLozK6gCAHRDVBRSx-cY9Q_rJqfAgk9V14Tamh68oMcEM3YBQubflHD7bzv3tw_vNqqGUhHrszfSNVu24MlDd4JwU_bDCreUEwuNABeuip7cs6Pr9-AtBqo1bUktMYZcDFnlOSvacV59lZJb1dhrudfBwQsKMxUVHjaqahGPTVHeeYhkvvV3fCDtJRavBLK5y_5orh12riy-gcU2g");
		restHeader.add("x-api-key", "scoe-hackathon-app");
		restHeader.add("x-gw-ims-org-id", "154340995B76EEF60A494007@AdobeOrg");
		restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");

		JSONObject obj = new JSONObject();
		obj.put("input_format", "text");
		obj.put("data", content);
		HttpEntity<String> entity = new HttpEntity<>(obj.toString(), restHeader);

		/******************************/
		ArrayList<String> arrlist = new ArrayList<String>();
		arrlist.add("STATUS OF AGREEMNT");
		arrlist.add("STATUS");
		arrlist.add("STATUS OF");
		arrlist.add("STATUS FOR");
		arrlist.add("AGREEMENT");

		if (content.equalsIgnoreCase("Users") || content.equalsIgnoreCase("Adobe Users")) {
			try {
				UserInfo userInfo = this.adobeSignService.getUsers();

				ArrayList<String> userEmail = new ArrayList<String>();

				for (int i = 0; i < userInfo.getUserSet().size(); i++) {
					userEmail.add(userInfo.getUserSet().get(i).getEmail());
				}
				return userEmail.toString();
			} catch (Exception e) {
				return "Invalid ID";
			}

		}
		if (content.length() > 43) {
			String agreementID = content.substring(content.length() - 44, content.length());
			String startingString = content.replace(agreementID, "");

			if (arrlist.contains(startingString.toUpperCase().trim())) {
				try {
					AgreementInfo agreementInfo = this.adobeSignService.getContractStatus(agreementID);
					System.out.print("Status of agreement" + "XYZ = " + agreementInfo.getStatus());
					return "Status of agreement " + agreementID + " is " + agreementInfo.getStatus();
				} catch (Exception e) {
					return "Invalid Agreement ID";
				}
			} else {
				return "FROM BOT";
			}
		}

		else {
			// Get Asset Id from collection
			String URL = "https://emerald-stage.adobe.io/collection/firefall_adobe_acrobat_sign/search";
			ResponseEntity<String> resource = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
			String jsonData = resource.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonData);
			String assetId = jsonNode.get(0).get("asset_id").asText();
			double score = jsonNode.get(0).get("score").asDouble();

			if (score > 0.90) {
				// Get Data using asset ID
				URL = "https://emerald-stage.adobe.io/collection/firefall_adobe_acrobat_sign/asset/" + assetId;
				ResponseEntity<String> resource1 = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
				String jsonData1 = resource1.getBody();
				// ObjectMapper objectMapper1 = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(jsonData1);
				String data = rootNode.get("data").asText();

				// Completion API
				Gson gson = new Gson();
				JsonObject dialogue = new JsonObject();
				dialogue.addProperty("question", data);
				JsonObject llmMetadata = new JsonObject();
				llmMetadata.addProperty("model_name", "gpt-35-turbo");
				llmMetadata.addProperty("temperature", 0.0);
				llmMetadata.addProperty("max_tokens", 2000);
				llmMetadata.addProperty("top_p", 1.0);
				llmMetadata.addProperty("frequency_penalty", 0);
				llmMetadata.addProperty("presence_penalty", 0);
				llmMetadata.addProperty("n", 1);
				llmMetadata.addProperty("llm_type", "azure_chat_openai");
				String[] stopArray = { "\n", "\t" };
				llmMetadata.add("stop", gson.toJsonTree(stopArray));
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("dialogue", dialogue);
				jsonObject.add("llm_metadata", llmMetadata);
				String jsonString = gson.toJson(jsonObject);
				HttpEntity<String> entity1 = new HttpEntity<>(jsonString.toString(), restHeader);
				URL = "https://firefall-stage.adobe.io/v1/completions";
				ResponseEntity<String> resource2 = restTemplate.exchange(URL, HttpMethod.POST, entity1, String.class);
				String jsonData2 = resource2.getBody();
				JsonNode rootNode1 = objectMapper.readTree(jsonData2);
				String contentValue = rootNode1.path("generations").path(0).path(0).path("message").path("content")
						.asText();
				return contentValue;
			} else {
				// Completion API
				Gson gson = new Gson();
				JsonObject dialogue = new JsonObject();
				dialogue.addProperty("question", content);
				JsonObject llmMetadata = new JsonObject();
				llmMetadata.addProperty("model_name", "gpt-35-turbo");
				llmMetadata.addProperty("temperature", 0.0);
				llmMetadata.addProperty("max_tokens", 2000);
				llmMetadata.addProperty("top_p", 1.0);
				llmMetadata.addProperty("frequency_penalty", 0);
				llmMetadata.addProperty("presence_penalty", 0);
				llmMetadata.addProperty("n", 1);
				llmMetadata.addProperty("llm_type", "azure_chat_openai");
				String[] stopArray = { "\n", "\t" };
				llmMetadata.add("stop", gson.toJsonTree(stopArray));
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("dialogue", dialogue);
				jsonObject.add("llm_metadata", llmMetadata);
				String jsonString = gson.toJson(jsonObject);
				HttpEntity<String> entity1 = new HttpEntity<>(jsonString.toString(), restHeader);
				URL = "https://firefall-stage.adobe.io/v1/completions";
				ResponseEntity<String> resource2 = restTemplate.exchange(URL, HttpMethod.POST, entity1, String.class);
				String jsonData2 = resource2.getBody();
				JsonNode rootNode1 = objectMapper.readTree(jsonData2);
				String contentValue = rootNode1.path("generations").path(0).path(0).path("message").path("content")
						.asText();
				return contentValue;
			}
		}
	}

}
