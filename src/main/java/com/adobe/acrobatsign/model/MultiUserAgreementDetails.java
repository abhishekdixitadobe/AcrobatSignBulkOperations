package com.adobe.acrobatsign.model;

import java.util.List;
import java.util.Map;

public class MultiUserAgreementDetails {
	
	private Map<String, AgreementForm> agreementFormMap;
	private Long totalAgreements;
	private List<UserAgreement> agreementList;
	private List<String> userEmails;
	private Map<String, Long> nextIndexMap;
	private List<LibraryDocument> libraryDocumentList;
	private Long totalTemplates;
	
	
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
	public Map<String, Long> getNextIndexMap() {
		return nextIndexMap;
	}
	public void setNextIndexMap(Map<String, Long> nextIndexMap) {
		this.nextIndexMap = nextIndexMap;
	}
	public List<LibraryDocument> getLibraryDocumentList() {
		return libraryDocumentList;
	}
	public void setLibraryDocumentList(List<LibraryDocument> libraryDocumentList) {
		this.libraryDocumentList = libraryDocumentList;
	}
	public Long getTotalTemplates() {
		return totalTemplates;
	}
	public void setTotalTemplates(Long totalTemplates) {
		this.totalTemplates = totalTemplates;
	}

}
