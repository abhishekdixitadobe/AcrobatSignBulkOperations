package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequestBody {

	private List<String> scope;

	private AgreementAssetsCriteria agreementAssetsCriteria;

	public AgreementAssetsCriteria getAgreementAssetsCriteria() {
		return agreementAssetsCriteria;
	}

	public List<String> getScope() {
		return scope;
	}

	public void setAgreementAssetsCriteria(AgreementAssetsCriteria agreementAssetsCriteria) {
		this.agreementAssetsCriteria = agreementAssetsCriteria;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

}
