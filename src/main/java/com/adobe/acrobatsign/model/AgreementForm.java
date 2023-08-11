package com.adobe.acrobatsign.model;

import java.util.List;

import org.json.simple.JSONArray;

public class AgreementForm {
	private List<UserAgreement> agreementIdList;
	List<UserWidget> widgetList;
	private Long totalAgreements;
	private Long nextIndex;
	List<JSONArray> agreementJSONArray;
	private String userWorkflow;
	private String agreementJSONString;

	public List<UserAgreement> getAgreementIdList() {
		return this.agreementIdList;
	}

	public List<JSONArray> getAgreementJSONArray() {
		return agreementJSONArray;
	}

	public String getAgreementJSONString() {
		return agreementJSONString;
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

	public void setAgreementJSONArray(List<JSONArray> agreementJSONArray) {
		this.agreementJSONArray = agreementJSONArray;
	}

	public void setAgreementJSONString(String agreementJSONString) {
		this.agreementJSONString = agreementJSONString;
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
