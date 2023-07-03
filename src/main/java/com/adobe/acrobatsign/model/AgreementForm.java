package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementForm {
	private List<UserAgreement> agreementIdList;
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

	public void setAgreementIdList(List<UserAgreement> agreementIdList) {
		this.agreementIdList = agreementIdList;
	}

	public void setNextIndex(Long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public void setTotalAgreements(Long totalAgreements) {
		this.totalAgreements = totalAgreements;
	}

}
