package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisplayParticipantSetInfo {

	private List<DisplayParticipantInfo> displayUserSetMemberInfos;
	private String displayUserSetName;

	public List<DisplayParticipantInfo> getDisplayUserSetMemberInfos() {
		return displayUserSetMemberInfos;
	}

	public String getDisplayUserSetName() {
		return displayUserSetName;
	}

	public void setDisplayUserSetMemberInfos(List<DisplayParticipantInfo> displayUserSetMemberInfos) {
		this.displayUserSetMemberInfos = displayUserSetMemberInfos;
	}

	public void setDisplayUserSetName(String displayUserSetName) {
		this.displayUserSetName = displayUserSetName;
	}
}
