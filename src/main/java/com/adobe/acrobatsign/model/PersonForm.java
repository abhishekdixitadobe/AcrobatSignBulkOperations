package com.adobe.acrobatsign.model;

import lombok.Data;

@Data
public class PersonForm {
	
    private String userEmail;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}



}
