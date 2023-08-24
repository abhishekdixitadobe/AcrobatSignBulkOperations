package com.adobe.acrobatsign.model;

public class SelectedAgreement {
	private String id;
	private String name;
	private String userEmail;
	private String userId;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserId() {
		return userId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
