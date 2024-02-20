package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementAssetsCriteria {

	private List<String> status;
	private List<String> type;
	private DateRangeFilter modifiedDate;
	private List<String> role;
	private String visibility;
	private Integer startIndex;
	private List<String> groupId;
	private Integer pageSize;

	public List<String> getGroupId() {
		return groupId;
	}

	public DateRangeFilter getModifiedDate() {
		return modifiedDate;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public List<String> getRole() {
		return role;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public List<String> getStatus() {
		return status;
	}

	public List<String> getType() {
		return type;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setGroupId(List<String> groupId) {
		this.groupId = groupId;
	}

	public void setModifiedDate(DateRangeFilter modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

}
