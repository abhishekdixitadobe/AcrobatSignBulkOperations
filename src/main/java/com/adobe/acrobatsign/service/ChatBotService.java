package com.adobe.acrobatsign.service;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class ChatBotService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotService.class);

	@Value(value = "${openai-service.gpt-model}")
	private String gptModel;

	@Value(value = "${openai-service.urls.base-url}")
	private String chatGPTURL;

	@Value(value = "${openai-service.api-key}")
	private String openAPIKey;

	@Value(value = "${firefall-services.x-api-key}")
	private String firefallAPIKey;

	@Value(value = "${firefall-services.access-token}")
	private String firefallAccessToken;

	@Value(value = "${firefall-services.x-gw-ims-org-id}")
	private String firefallIMSOrgId;

	@Value(value = "${firefall-services.completions-url}")
	private String firefallCompletionsUrl;

	@Value(value = "${firefall-services.emerald-colleciton-search}")
	private String emeraldCollectionSearch;

	@Value(value = "${firefall-services.emerald-asset-search}")
	private String emeraldAssetSearch;

	@Value(value = "${firefall-services.llm.model_name}")
	private String modelName;

	@Value(value = "${firefall-services.llm.temperature}")
	private double temperature;

	@Value(value = "${firefall-services.llm.max_tokens}")
	private int maxTokens;

	@Value(value = "${firefall-services.llm.top_p}")
	private double topP;

	@Value(value = "${firefall-services.llm.frequency_penalty}")
	private double frequencyPenalty;

	@Value(value = "${firefall-services.llm.presence_penalty}")
	private double presencePenalty;

	@Value(value = "${firefall-services.llm.llm_type}")
	private String llmType;
	
	@Value(value = "${firefall-services.conversation}")
	private String conversation;

	@Autowired
	AdobeSignService adobeSignService;
	
	@Autowired
	RestTemplate restTemplate;
	
	private HttpHeaders setHeaders() {
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(),
				Constants.BEARER + this.firefallAccessToken);
		restHeader.add("x-api-key", this.firefallAPIKey);
		restHeader.add("x-gw-ims-org-id", this.firefallIMSOrgId);
		restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(),
				RestApiUtils.MimeType.APPLICATIONJSON.toString());
		return restHeader;
	}
	
	public void getQueries(Long conversationId) {
		String endPoint = this.conversation + conversationId + "/" + "queries";
		HttpEntity<String> entity = new HttpEntity<>(setHeaders());
		ResponseEntity<String> resource = restTemplate.exchange(endPoint, HttpMethod.GET, entity, String.class);
	} 

	public String chatWithGpt3(String content) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		

		JSONObject obj = new JSONObject();
		obj.put("input_format", "text");
		obj.put("data", content);
		HttpEntity<String> entity = new HttpEntity<>(obj.toString(), setHeaders());

		/******************************/
		ArrayList<String> arrlist = new ArrayList<String>();
		arrlist.add("STATUS OF AGREEMNT");
		arrlist.add("STATUS");
		arrlist.add("STATUS OF");
		arrlist.add("STATUS FOR");
		arrlist.add("AGREEMENT");

		if (content.equalsIgnoreCase("Users") || content.equalsIgnoreCase("Adobe Users")) {
			return this.getUserInfo();
		}
		if (arrlist.contains(content.toUpperCase().trim())) {
			String agreementID = content.substring(content.length() - 44, content.length());
			String startingString = content.replace(agreementID, "");

			/*
			 * if (!arrlist.contains(startingString.toUpperCase().trim())) { return
			 * "FROM BOT"; }
			 */
			try {
				AgreementInfo agreementInfo = this.adobeSignService.getContractStatus(agreementID);
				LOGGER.info("Status of agreement" + "XYZ = " + agreementInfo.getStatus());
				return "Status of agreement " + agreementID + " is " + agreementInfo.getStatus();
			} catch (Exception e) {
				return "Invalid Agreement ID";
			}
		}
		// Get Asset Id from collection
		ResponseEntity<String> resource = restTemplate.exchange(this.emeraldCollectionSearch, HttpMethod.POST, entity,
				String.class);
		String jsonData = resource.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(jsonData);
		String assetId = jsonNode.get(0).get("asset_id").asText();
		double score = jsonNode.get(0).get("score").asDouble();

		if (score > 0.90) {
			// Get Data using asset ID
			ResponseEntity<String> resource1 = restTemplate.exchange(this.emeraldAssetSearch, HttpMethod.GET, entity,
					String.class);
			String jsonData1 = resource1.getBody();
			// ObjectMapper objectMapper1 = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonData1);
			String data = rootNode.get("data").asText();

			// Completion API
			Gson gson = new Gson();
			JsonObject dialogue = new JsonObject();
			dialogue.addProperty("question", data);

			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dialogue", dialogue);
			jsonObject.add("llm_metadata", this.setLLMData());
			String jsonString = gson.toJson(jsonObject);
			HttpEntity<String> entity1 = new HttpEntity<>(jsonString.toString(), setHeaders());
			ResponseEntity<String> resource2 = restTemplate.exchange(this.firefallCompletionsUrl, HttpMethod.POST,
					entity1, String.class);
			String jsonData2 = resource2.getBody();
			JsonNode rootNode1 = objectMapper.readTree(jsonData2);
			String contentValue = rootNode1.path("generations").path(0).path(0).path("message").path("content")
					.asText();
			return contentValue;
		}
		// Completion API
		Gson gson = new Gson();
		JsonObject dialogue = new JsonObject();
		dialogue.addProperty("question", content + " in Adobe Sign");
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("dialogue", dialogue);
		jsonObject.add("llm_metadata", this.setLLMData());
		String jsonString = gson.toJson(jsonObject);
		HttpEntity<String> entity1 = new HttpEntity<>(jsonString.toString(), setHeaders());
		ResponseEntity<String> resource2 = restTemplate.exchange(this.firefallCompletionsUrl, HttpMethod.POST, entity1,
				String.class);
		String jsonData2 = resource2.getBody();
		JsonNode rootNode1 = objectMapper.readTree(jsonData2);
		String contentValue = rootNode1.path("generations").path(0).path(0).path("message").path("content").asText();
		return contentValue;

	}

	private String getUserInfo() {
		ArrayList<String> userEmail = new ArrayList<String>();
		try {
			UserInfo userInfo = this.adobeSignService.getUsers();
			for (int i = 0; i < userInfo.getUserSet().size(); i++) {
				userEmail.add(userInfo.getUserSet().get(i).getEmail());
			}
		} catch (Exception e) {
			return "Invalid ID";
		}
		return userEmail.toString();
	}

	private JsonObject setLLMData() {
		Gson gson = new Gson();
		JsonObject llmMetadata = new JsonObject();
		llmMetadata.addProperty(Constants.MODEL_NAME, this.modelName);
		llmMetadata.addProperty(Constants.TEMPERATURE, this.temperature);
		llmMetadata.addProperty(Constants.MAX_TOKENS, this.maxTokens);
		llmMetadata.addProperty(Constants.TOP_P, this.topP);
		llmMetadata.addProperty(Constants.FREQUENCY_PENALTY, this.frequencyPenalty);
		llmMetadata.addProperty(Constants.PRESENCE_PENALTY, this.presencePenalty);
		llmMetadata.addProperty(Constants.LLM_TYPE, this.llmType);
		return llmMetadata;
	}

}
