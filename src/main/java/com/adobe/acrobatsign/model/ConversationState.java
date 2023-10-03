package com.adobe.acrobatsign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConversationState {
	
	@Id
	@Column(name = "conversationId", columnDefinition = "conversationId")
	private Long conversationId;
	
	private String conversationName;

	public Long getConversationId() {
		return conversationId;
	}

	public void setConversationId(Long conversationId) {
		this.conversationId = conversationId;
	}

	public String getConversationName() {
		return conversationName;
	}

	public void setConversationName(String conversationName) {
		conversationName = conversationName;
	}
}
