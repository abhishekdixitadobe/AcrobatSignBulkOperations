package com.adobe.acrobatsign.model;

import java.util.List;
import java.util.Map;

public class MultiUserAgreementDetails {
	
	private Map<String, AgreementForm> agreementFormMap;
	private Long totalAgreements;
	List<UserAgreement> agreementList;
	List<String> userEmails;
	
	
	public Map<String, AgreementForm> getAgreementFormMap() {
		return agreementFormMap;
	}
	public void setAgreementFormMap(Map<String, AgreementForm> agreementFormMap) {
		this.agreementFormMap = agreementFormMap;
	}
	public Long getTotalAgreements() {
		return totalAgreements;
	}
	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}
	public List<UserAgreement> getAgreementList() {
		return agreementList;
	}
	public void setAgreementList(List<UserAgreement> agreementList) {
		this.agreementList = agreementList;
	}
	public List<String> getUserEmails() {
		return userEmails;
	}
	public void setUserEmails(List<String> userEmails) {
		this.userEmails = userEmails;
	}

}
