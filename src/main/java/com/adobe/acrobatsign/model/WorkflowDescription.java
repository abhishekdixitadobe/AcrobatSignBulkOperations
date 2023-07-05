package com.adobe.acrobatsign.model;

public class WorkflowDescription {

	private String displayName;

	private String name;

	private String scope;

	private String created;

	public String getCreated() {
		return this.created;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getName() {
		return this.name;
	}

	public String getScope() {
		return this.scope;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
