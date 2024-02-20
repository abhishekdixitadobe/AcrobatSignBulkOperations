package com.adobe.acrobatsign.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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

	public List<UserAgreement> getAgreementList() {
		return agreementList;
	}

	public List<LibraryDocument> getLibraryDocumentList() {
		return libraryDocumentList;
	}

	public Map<String, Long> getNextIndexMap() {
		return nextIndexMap;
	}

	public Long getTotalAgreements() {
		return totalAgreements;
	}

	public Long getTotalTemplates() {
		return totalTemplates;
	}

	public List<String> getUserEmails() {
		return userEmails;
	}

	public void setAgreementFormMap(Map<String, AgreementForm> agreementFormMap) {
		this.agreementFormMap = agreementFormMap;
	}

	public void setAgreementList(List<UserAgreement> agreementList) {
		this.agreementList = agreementList;
	}

	public void setLibraryDocumentList(List<LibraryDocument> libraryDocumentList) {
		this.libraryDocumentList = libraryDocumentList;
	}

	public void setNextIndexMap(Map<String, Long> nextIndexMap) {
		this.nextIndexMap = nextIndexMap;
	}

	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}

	public void setTotalTemplates(Long totalTemplates) {
		this.totalTemplates = totalTemplates;
	}

	public void setUserEmails(List<String> userEmails) {
		this.userEmails = userEmails;
	}

}
