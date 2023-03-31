package com.adobe.acrobatsign.model;

import java.util.List;

public class AgreementAssetsCriteria {
	
	private List<String> status;
	private List<String> type;
	private DateRangeFilter modifiedDate;
	
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

}
