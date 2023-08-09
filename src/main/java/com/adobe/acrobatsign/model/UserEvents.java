package com.adobe.acrobatsign.model;

import java.util.List;

public class UserEvents {

	List<UserEvent> events;  
	
	public List<UserEvent> getEvents() {  
		return events ;
	}

	public void getEvents(List<UserEvent> events ) {
		this.events  = events ;
	}
}