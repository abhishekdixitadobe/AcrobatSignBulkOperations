package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWorkflows {

	List<UserWorkflow> userWorkflowList;

	public List<UserWorkflow> getUserWorkflowList() {
		return userWorkflowList;
	}

	public void setUserWorkflowList(List<UserWorkflow> userWorkflowList) {
		this.userWorkflowList = userWorkflowList;
	}

}
