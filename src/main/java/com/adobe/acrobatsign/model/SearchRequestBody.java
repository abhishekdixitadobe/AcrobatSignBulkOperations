package com.adobe.acrobatsign.model;

import java.util.List;

public class SearchRequestBody {
	
	private List<String> scope;
	
	private AgreementAssetsCriteria agreementAssetsCriteria;

	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public AgreementAssetsCriteria getAgreementAssetsCriteria() {
		return agreementAssetsCriteria;
	}

	public void setAgreementAssetsCriteria(AgreementAssetsCriteria agreementAssetsCriteria) {
		this.agreementAssetsCriteria = agreementAssetsCriteria;
	}

}
