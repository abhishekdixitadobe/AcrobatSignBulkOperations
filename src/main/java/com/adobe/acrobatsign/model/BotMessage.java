package com.adobe.acrobatsign.model;

public class BotMessage {

	private String message;
	
	private String conversationId;

	public BotMessage() {
	}

	public BotMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

}
