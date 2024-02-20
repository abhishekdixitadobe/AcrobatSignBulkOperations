package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateRange {
	private String gt;
	private String lt;

	public String getGt() {
		return gt;
	}

	public String getLt() {
		return lt;
	}

	public void setGt(String gt) {
		this.gt = gt;
	}

	public void setLt(String lt) {
		this.lt = lt;
	}
}
