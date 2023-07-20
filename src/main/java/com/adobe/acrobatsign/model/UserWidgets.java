package com.adobe.acrobatsign.model;

import java.util.List;

public class UserWidgets {
	private PageInfo page;
	List<UserWidget> userWidgetList;

	public PageInfo getPage() {
		return page;
	}

	public List<UserWidget> getUserWidgetList() {
		return userWidgetList;
	}

	public void setPage(PageInfo page) {
		this.page = page;
	}

	public void setUserWidgetList(List<UserWidget> userWidgetList) {
		this.userWidgetList = userWidgetList;
	}
}
