package com.adobe.acrobatsign.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementDetails {
	
	List<AgreementDetail> details;
	
	public List<AgreementDetail> getDetails() {
		return details;
	}
	public void setDetails(List<AgreementDetail> details) {
		this.details = details;
	}



}
