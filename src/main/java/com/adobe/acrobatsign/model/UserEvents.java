package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEvents {

	List<UserEvent> events;

	public List<UserEvent> getEvents() {
		return events;
	}

	public void getEvents(List<UserEvent> events) {
		this.events = events;
	}
}