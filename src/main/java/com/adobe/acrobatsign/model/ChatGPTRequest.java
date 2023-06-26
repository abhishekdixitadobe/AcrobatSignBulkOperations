package com.adobe.acrobatsign.model;

import java.util.List;

public class ChatGPTRequest {

	private String model;
	private List<ChatmessageVO> messages;

	public List<ChatmessageVO> getMessages() {
		return messages;
	}

	public String getModel() {
		return model;
	}

	public void setMessages(List<ChatmessageVO> messages) {
		this.messages = messages;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
