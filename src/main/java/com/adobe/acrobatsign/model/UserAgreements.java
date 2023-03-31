package com.adobe.acrobatsign.model;

import java.util.List;

public class UserAgreements {
	private PageInfo page;
	private List<UserAgreement> userAgreementList;
	public PageInfo getPage() {
		return page;
	}
	public void setPage(PageInfo page) {
		this.page = page;
	}
	public List<UserAgreement> getUserAgreementList() {
		return userAgreementList;
	}
	public void setUserAgreementList(List<UserAgreement> userAgreementList) {
		this.userAgreementList = userAgreementList;
	}
	
}
