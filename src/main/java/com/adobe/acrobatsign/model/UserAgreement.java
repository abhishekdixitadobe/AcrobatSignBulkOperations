package com.adobe.acrobatsign.model;

import java.util.List;

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
	
	private String isChecked;
	
	public String getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}
	private List<DisplayParticipantSetInfo> displayParticipantSetInfos;
	public String getDisplayDate() {
		return displayDate;
	}
	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}
	public boolean isEsign() {
		return esign;
	}
	public void setEsign(boolean esign) {
		this.esign = esign;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String getLatestVersionId() {
		return latestVersionId;
	}
	public void setLatestVersionId(String latestVersionId) {
		this.latestVersionId = latestVersionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<DisplayParticipantSetInfo> getDisplayParticipantSetInfos() {
		return displayParticipantSetInfos;
	}
	public void setDisplayParticipantSetInfos(List<DisplayParticipantSetInfo> displayParticipantSetInfos) {
		this.displayParticipantSetInfos = displayParticipantSetInfos;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
