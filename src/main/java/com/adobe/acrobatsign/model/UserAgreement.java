package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAgreement {
	private String displayDate;
	private boolean esign;
	private String groupId;
	private boolean hidden;
	private String latestVersionId;
	private String name;
	private String id;
	private String parentId;
	private String status;
	private String type;
	private String modifiedDate;
	private String userEmail;
	private String workflowId;
	private String workflowName;

	private String isChecked;

	private List<DisplayParticipantSetInfo> displayParticipantSetInfos;

	public String getDisplayDate() {
		return this.displayDate;
	}

	public List<DisplayParticipantSetInfo> getDisplayParticipantSetInfos() {
		return this.displayParticipantSetInfos;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getId() {
		return this.id;
	}

	public String getIsChecked() {
		return this.isChecked;
	}

	public String getLatestVersionId() {
		return this.latestVersionId;
	}

	public String getModifiedDate() {
		return this.modifiedDate;
	}

	public String getName() {
		return this.name;
	}

	public String getParentId() {
		return this.parentId;
	}

	public String getStatus() {
		return this.status;
	}

	public String getType() {
		return this.type;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public String getWorkflowId() {
		return this.workflowId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public boolean isEsign() {
		return this.esign;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}

	public void setDisplayParticipantSetInfos(List<DisplayParticipantSetInfo> displayParticipantSetInfos) {
		this.displayParticipantSetInfos = displayParticipantSetInfos;
	}

	public void setEsign(boolean esign) {
		this.esign = esign;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public void setLatestVersionId(String latestVersionId) {
		this.latestVersionId = latestVersionId;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
}
