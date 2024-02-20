package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementForm {
	private List<UserAgreement> agreementIdList;
	List<UserWidget> widgetList;
	private Long totalAgreements;
	private Long nextIndex;
	List<LibraryDocument> libraryTemplateList;
	private String userWorkflow;

	public List<UserAgreement> getAgreementIdList() {
		return this.agreementIdList;
	}

	public List<LibraryDocument> getLibraryTemplateList() {
		return libraryTemplateList;
	}

	public Long getNextIndex() {
		return this.nextIndex;
	}

	public Long getTotalAgreements() {
		return this.totalAgreements;
	}

	public String getUserWorkflow() {
		return userWorkflow;
	}

	public List<UserWidget> getWidgetList() {
		return widgetList;
	}

	public void setAgreementIdList(List<UserAgreement> agreementIdList) {
		this.agreementIdList = agreementIdList;
	}

	public void setLibraryTemplateList(List<LibraryDocument> libraryTemplateList) {
		this.libraryTemplateList = libraryTemplateList;
	}

	public void setNextIndex(Long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}

	public void setUserWorkflow(String userWorkflow) {
		this.userWorkflow = userWorkflow;
	}

	public void setWidgetList(List<UserWidget> widgetList) {
		this.widgetList = widgetList;
	}

}
