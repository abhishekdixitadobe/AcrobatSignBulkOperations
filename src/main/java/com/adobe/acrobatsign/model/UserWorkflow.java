package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWorkflow {

	private String created;
	private String description;
	private String displayName;
	private String id;
	private String modified;
	private String name;
	private String scope;
	private String status;
	private String originatorId;
	private String originatorName;
	private String scopeId;

	public String getCreated() {
		return created;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public String getModified() {
		return modified;
	}

	public String getName() {
		return name;
	}

	public String getOriginatorId() {
		return originatorId;
	}

	public String getOriginatorName() {
		return originatorName;
	}

	public String getScope() {
		return scope;
	}

	public String getScopeId() {
		return scopeId;
	}

	public String getStatus() {
		return status;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOriginatorId(String originatorId) {
		this.originatorId = originatorId;
	}

	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
