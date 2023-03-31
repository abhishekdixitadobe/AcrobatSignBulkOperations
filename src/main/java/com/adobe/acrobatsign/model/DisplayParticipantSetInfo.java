package com.adobe.acrobatsign.model;

import java.util.List;

public class DisplayParticipantSetInfo {

	private List<DisplayParticipantInfo> displayUserSetMemberInfos;
	private String displayUserSetName;
	public List<DisplayParticipantInfo> getDisplayUserSetMemberInfos() {
		return displayUserSetMemberInfos;
	}
	public void setDisplayUserSetMemberInfos(List<DisplayParticipantInfo> displayUserSetMemberInfos) {
		this.displayUserSetMemberInfos = displayUserSetMemberInfos;
	}
	public String getDisplayUserSetName() {
		return displayUserSetName;
	}
	public void setDisplayUserSetName(String displayUserSetName) {
		this.displayUserSetName = displayUserSetName;
	}
}
