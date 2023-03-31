package com.adobe.acrobatsign.model;

import lombok.Data;

@Data
public class MemberInfo {

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
