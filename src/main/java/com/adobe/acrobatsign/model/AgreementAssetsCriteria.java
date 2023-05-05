package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementAssetsCriteria {
	
	private List<String> status;
	private List<String> type;
	private DateRangeFilter modifiedDate;
	private List<String> role;
	private String visibility;
	
	public List<String> getRole() {
		return role;
	}
	public void setRole(List<String> role) {
		this.role = role;
	}
	public List<String> getStatus() {
		return status;
	}
	public void setStatus(List<String> status) {
		this.status = status;
	}
	public List<String> getType() {
		return type;
	}
	public void setType(List<String> type) {
		this.type = type;
	}
	public DateRangeFilter getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(DateRangeFilter modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

}
