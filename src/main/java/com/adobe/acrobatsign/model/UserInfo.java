package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String company;
	private String accountId;
	private Boolean isAccountAdmin;

	public String getAccountId() {
		return accountId;
	}

	public String getCompany() {
		return company;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getId() {
		return id;
	}

	public Boolean getIsAccountAdmin() {
		return isAccountAdmin;
	}

	public String getLastName() {
		return lastName;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIsAccountAdmin(Boolean isAccountAdmin) {
		this.isAccountAdmin = isAccountAdmin;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
