package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement {
	private String id;
	private List<String> role;
	private String hidden;
	private String groupId;
	private List<ParticipantSet> participantSet;
	private String externalId;
	private String subTypes;
	private String type;
	private String userId;
	private String parentId;
	private String createdDate;
	private String modifiedDate;
	private String name;
	private String workflowId;
	private String status;
	private String expirationDate;

	public String getCreatedDate() {
		return createdDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getHidden() {
		return hidden;
	}

	public String getId() {
		return id;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getName() {
		return name;
	}

	public String getParentId() {
		return parentId;
	}

	public List<ParticipantSet> getParticipantSet() {
		return participantSet;
	}

	public List<String> getRole() {
		return role;
	}

	public String getStatus() {
		return status;
	}

	public String getSubTypes() {
		return subTypes;
	}

	public String getType() {
		return type;
	}

	public String getUserId() {
		return userId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setParticipantSet(List<ParticipantSet> participantSet) {
		this.participantSet = participantSet;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubTypes(String subTypes) {
		this.subTypes = subTypes;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

}
