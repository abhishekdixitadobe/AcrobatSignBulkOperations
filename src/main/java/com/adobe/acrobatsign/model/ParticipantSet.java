package com.adobe.acrobatsign.model;

import java.util.List;

import lombok.Data;

@Data
public class ParticipantSet {

	private String role;
	
	private String order;
	
	private List<MemberInfo> memberInfos;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<MemberInfo> getMemberInfos() {
		return memberInfos;
	}

	public void setMemberInfos(List<MemberInfo> memberInfos) {
		this.memberInfos = memberInfos;
	}
}
