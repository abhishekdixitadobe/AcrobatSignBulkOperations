package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementForm {
	private List<UserAgreement> agreementIdList;
	private Long totalAgreements;
	private Long nextIndex;

	public List<UserAgreement> getAgreementIdList() {
		return agreementIdList;
	}

	public void setAgreementIdList(List<UserAgreement> agreementIdList) {
		this.agreementIdList = agreementIdList;
	}

	public Long getTotalAgreements() {
		return totalAgreements;
	}

	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}

	public Long getNextIndex() {
		return nextIndex;
	}

	public void setNextIndex(Long nextIndex) {
		this.nextIndex = nextIndex;
	}

}
