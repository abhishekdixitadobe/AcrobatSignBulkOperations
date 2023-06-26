package com.adobe.acrobatsign.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.adobe.acrobatsign.model.ChatGPTRequest;
import com.adobe.acrobatsign.model.ChatmessageVO;
import com.adobe.acrobatsign.util.RestApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public String chatWithGpt3(ChatmessageVO message) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		ChatGPTRequest chatGPTRequest = new ChatGPTRequest();
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), "Bearer " + this.openAPIKey);
		restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		chatGPTRequest.setModel(this.gptModel);
		message.setRole("user");
		List<ChatmessageVO> chatMessage = new ArrayList<>();
		chatMessage.add(message);
		chatGPTRequest.setMessages(chatMessage);
		HttpEntity<String> entity = new HttpEntity<>(this.jsonMapper.writeValueAsString(chatGPTRequest), restHeader);
		ResponseEntity<String> resource = restTemplate.exchange(this.chatGPTURL, HttpMethod.POST, entity, String.class);
		org.json.JSONObject jsonObject = new org.json.JSONObject(resource.getBody());
		String response = ((org.json.JSONObject) ((org.json.JSONObject) ((org.json.JSONArray) jsonObject.get("choices"))
				.get(0)).get("message")).getString("content");
		return response;
	}

}
