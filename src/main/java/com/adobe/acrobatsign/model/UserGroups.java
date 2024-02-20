package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroups {

	List<UserGroup> groupInfoList;

	public List<UserGroup> getGroupInfoList() {
		return groupInfoList;
	}

	public void setGroupInfoList(List<UserGroup> groupInfoList) {
		this.groupInfoList = groupInfoList;
	}
}