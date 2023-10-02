package com.adobe.acrobatsign.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
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
import com.adobe.acrobatsign.model.BotMessage;
import com.adobe.acrobatsign.model.ConversationQuery;
import com.adobe.acrobatsign.model.UserInfo;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	@Value(value = "${firefall-services.statefull-completions}")
	private String statefullCompletionChat;

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
	
	public List<ConversationQuery> getQueries(Long conversationId) {
		String endPoint = this.conversation + conversationId + "/" + "queries";
		HttpEntity<String> entity = new HttpEntity<>(setHeaders());
		ResponseEntity<String> resource = restTemplate.exchange(endPoint, HttpMethod.GET, entity, String.class);
		String jsonData1 = resource.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		ConversationQuery query = null;
		List<ConversationQuery> queryList = new ArrayList<>();
		try {
			JsonNode rootNode = objectMapper.readTree(jsonData1);
			for (int i = 0; i < rootNode.size(); i++) {
				query = new ConversationQuery();
				query.setQueryId(rootNode.get(i).get("query_id").asText());
				query.setQueryName(rootNode.get(i).get("dialogue").get("messages").get(0).get("content").asText());
				query.setMessage(rootNode.get(i).get("dialogue").get("generations").get("generations").get(0).get(0).get("message").get("content").asText());
				queryList.add(query);
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return queryList;
	} 

	private HttpEntity<String> getEntity(String content){
		JSONObject obj = new JSONObject();
		obj.put("input_format", "text");
		obj.put("data", content);
		return new HttpEntity<>(obj.toString(), setHeaders());
	}
	
	private double emeraldScore(String content) throws JsonMappingException, JsonProcessingException {
		// Get Asset Id from collection
		ResponseEntity<String> resource = restTemplate.exchange(this.emeraldCollectionSearch, HttpMethod.POST, getEntity(content),
				String.class);
		String jsonData = resource.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(jsonData);
		String assetId = jsonNode.get(0).get("asset_id").asText();
		return jsonNode.get(0).get("score").asDouble();
	}
	
	private String emeraldAssetSearch(String content) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		// Get Data using asset ID
		ResponseEntity<String> resource1 = restTemplate.exchange(this.emeraldAssetSearch, HttpMethod.GET, getEntity(content),
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
	
	public String stateFullConversation(BotMessage botmessage) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String content = botmessage.getMessage();
		Long conversationId = Long.parseLong(botmessage.getConversationId());
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
			try {
				AgreementInfo agreementInfo = this.adobeSignService.getContractStatus(agreementID);
				LOGGER.info("Status of agreement" + "XYZ = " + agreementInfo.getStatus());
				return "Status of agreement " + agreementID + " is " + agreementInfo.getStatus();
			} catch (Exception e) {
				return "Invalid Agreement ID";
			}
		}
		if (emeraldScore(content) > 0.90) {
			content = emeraldAssetSearch(content);
		} else {
			content =  content + " in Adobe Sign";
		}
		// Completion API
		Gson gson = new Gson();
		
		JSONObject conversationObj = new JSONObject();
		
		JSONArray messageArray = new JSONArray();
		JSONObject messageObj = new JSONObject();
		
		messageObj.put("content", content);
		messageObj.put("role", "user");
		messageArray.add(messageObj);
		
		conversationObj.put("messages", messageArray);
		conversationObj.put("conversation_identifier", conversationId);
		
		HttpEntity<String> chatEntity = new HttpEntity<>(conversationObj.toString(), setHeaders());
		ResponseEntity<String> resource2 = restTemplate.exchange(this.statefullCompletionChat, HttpMethod.POST, chatEntity,
				String.class);
		String jsonData2 = resource2.getBody();
		JsonNode rootNode1 = objectMapper.readTree(jsonData2);
		String contentValue = rootNode1.path("generations").path(0).path(0).path("message").path("content").asText();
		return contentValue;

	}
	public String chatWithGpt3(String content) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
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
		if (emeraldScore(content) > 0.90) {
			content = emeraldAssetSearch(content);
		} else {
			content =  content + " in Adobe Sign";
		}
		// Completion API
		Gson gson = new Gson();
		JsonObject dialogue = new JsonObject();
		dialogue.addProperty("question", content);
		
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
