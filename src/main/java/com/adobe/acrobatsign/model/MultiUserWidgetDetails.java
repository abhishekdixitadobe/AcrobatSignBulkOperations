package com.adobe.acrobatsign.model;

import java.util.List;
import java.util.Map;

public class MultiUserWidgetDetails {

	private Map<String, AgreementForm> agreementFormMap;
	private Long totalWidgets;
	private List<UserWidget> widgetList;
	private List<String> userEmails;
	private Map<String, String> nextIndexMap;

	public Map<String, AgreementForm> getAgreementFormMap() {
		return agreementFormMap;
	}

	public Map<String, String> getNextIndexMap() {
		return nextIndexMap;
	}

	public Long getTotalWidgets() {
		return totalWidgets;
	}

	public List<String> getUserEmails() {
		return userEmails;
	}

	public List<UserWidget> getWidgetList() {
		return widgetList;
	}

	public void setAgreementFormMap(Map<String, AgreementForm> agreementFormMap) {
		this.agreementFormMap = agreementFormMap;
	}

	public void setNextIndexMap(Map<String, String> nextIndexMap) {
		this.nextIndexMap = nextIndexMap;
	}

	public void setTotalWidgets(Long totalWidgets) {
		this.totalWidgets = totalWidgets;
	}

	public void setUserEmails(List<String> userEmails) {
		this.userEmails = userEmails;
	}

	public void setWidgetList(List<UserWidget> widgetList) {
		this.widgetList = widgetList;
	}

}
