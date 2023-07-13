package com.adobe.acrobatsign.model;

import java.util.List;

public class UsersInfo {

	PageInfo page;

	List<UserInfo> userInfoList;

	public PageInfo getPage() {
		return page;
	}

	public List<UserInfo> getUserInfoList() {
		return userInfoList;
	}

	public void setPage(PageInfo page) {
		this.page = page;
	}

	public void setUserInfoList(List<UserInfo> userInfoList) {
		this.userInfoList = userInfoList;
	}

}
