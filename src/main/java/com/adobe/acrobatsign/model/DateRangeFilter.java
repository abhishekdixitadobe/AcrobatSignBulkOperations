package com.adobe.acrobatsign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateRangeFilter {

	private DateRange range;

	public DateRange getRange() {
		return range;
	}

	public void setRange(DateRange range) {
		this.range = range;
	}
}
