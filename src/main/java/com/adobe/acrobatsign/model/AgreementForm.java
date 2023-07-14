package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementForm {
	private List<UserAgreement> agreementIdList;
	List<UserWidget> widgetList;
	private Long totalAgreements;
	private Long nextIndex;

	public List<UserAgreement> getAgreementIdList() {
		return this.agreementIdList;
	}

	public Long getNextIndex() {
		return this.nextIndex;
	}

	public Long getTotalAgreements() {
		return this.totalAgreements;
	}

	public List<UserWidget> getWidgetList() {
		return widgetList;
	}

	public void setAgreementIdList(List<UserAgreement> agreementIdList) {
		this.agreementIdList = agreementIdList;
	}

	public void setNextIndex(Long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}

	public void setWidgetList(List<UserWidget> widgetList) {
		this.widgetList = widgetList;
	}

}
