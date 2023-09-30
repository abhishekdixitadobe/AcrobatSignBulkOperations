package com.adobe.acrobatsign.model;

import java.util.List;

public class Conversation {

	private long conversation_id;
	
	private String conversation_name;
	
	private List<Message> messages;

	public long getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(long conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getConversation_name() {
		return conversation_name;
	}

	public void setConversation_name(String conversation_name) {
		this.conversation_name = conversation_name;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

}
