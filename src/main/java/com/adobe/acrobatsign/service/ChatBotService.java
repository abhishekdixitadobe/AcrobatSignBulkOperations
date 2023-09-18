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

import com.adobe.acrobatsign.model.AgreementInfo;
import com.adobe.acrobatsign.model.ChatGPTRequest;
import com.adobe.acrobatsign.model.ChatmessageVO;
import com.adobe.acrobatsign.model.UserInfo;
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
	
	@Autowired
	AdobeSignService adobeSignService;
	

	public String chatWithGpt3(String content) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		ChatGPTRequest chatGPTRequest = new ChatGPTRequest();
		HttpHeaders restHeader = new HttpHeaders();
		restHeader.add(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), "Bearer " + this.openAPIKey);
		restHeader.add(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), "application/json");
		chatGPTRequest.setModel(this.gptModel);
		ChatmessageVO message = new ChatmessageVO();
		message.setRole("user");
		message.setContent(content);
		List<ChatmessageVO> chatMessage = new ArrayList<>();
		chatMessage.add(message);
		chatGPTRequest.setMessages(chatMessage);
		HttpEntity<String> entity = new HttpEntity<>(this.jsonMapper.writeValueAsString(chatGPTRequest), restHeader);
		
		
		
		/******************************/
		 ArrayList<String> arrlist = new ArrayList<String>();
		 arrlist.add("STATUS OF AGREEMNT");
		 arrlist.add("STATUS");
		 arrlist.add("STATUS OF");
		 arrlist.add("STATUS FOR");
		 arrlist.add("AGREEMENT");
		    
		 if (content.equalsIgnoreCase("Users") || content.equalsIgnoreCase("Adobe Users"))
		 {
			 try {
				 UserInfo userInfo = adobeSignService.getUsers();
				 
				 ArrayList<String> userEmail = new ArrayList<String>();
				 
				 for (int i = 0; i < userInfo.getUserSet().size(); i++) {
					 userEmail.add(userInfo.getUserSet().get(i).getEmail());
					}
				 return userEmail.toString();
				 }
				 catch (Exception e)
				 {
					 return "Invalid ID";
				 }
			 
		 }
		 else if (content.length() > 43)
		 {
			 String agreementID = content.substring(content.length()-44, content.length()); 
			 String startingString = content.replace(agreementID, "");
			 
			 if (arrlist.contains(startingString.toUpperCase().trim()))   
			 { 
				 try {
				 AgreementInfo agreementInfo = adobeSignService.getContractStatus(agreementID);	         
		         System.out.print("Status of agreement" + "XYZ = " + agreementInfo.getStatus());			
				 return "Status of agreement " + agreementID + " is " + agreementInfo.getStatus();
				 }
				 catch (Exception e)
				 {
					 return "Invalid Agreement ID";
				 }
			}
			else
			{
				//return "From Trained Bot"; // Call BOT
			/*	ResponseEntity<String> resource = restTemplate.exchange(this.chatGPTURL, HttpMethod.POST, entity, String.class);
				org.json.JSONObject jsonObject = new org.json.JSONObject(resource.getBody());
				String response = ((org.json.JSONObject) ((org.json.JSONObject) ((org.json.JSONArray) jsonObject.get("choices"))
						.get(0)).get("message")).getString("content");
				return response;*/
				return "FROM BOT";
				
			} 
		 }
		
		else
		{
			//return "From Trained Bot"; // Call BOT
		/*	ResponseEntity<String> resource = restTemplate.exchange(this.chatGPTURL, HttpMethod.POST, entity, String.class);
			org.json.JSONObject jsonObject = new org.json.JSONObject(resource.getBody());
			String response = ((org.json.JSONObject) ((org.json.JSONObject) ((org.json.JSONArray) jsonObject.get("choices"))
					.get(0)).get("message")).getString("content");
			return response;  */
			return "FROM BOT";
			
		}
		
		/******************************/
		
		//return response;
	}

}
