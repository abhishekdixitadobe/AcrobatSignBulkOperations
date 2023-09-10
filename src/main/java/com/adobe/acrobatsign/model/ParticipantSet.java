package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantSet {

	private String role;

	private String order;

	private List<MemberInfo> memberInfos;

	public List<MemberInfo> getMemberInfos() {
		return memberInfos;
	}

	public String getOrder() {
		return order;
	}

	public String getRole() {
		return role;
	}

	public void setMemberInfos(List<MemberInfo> memberInfos) {
		this.memberInfos = memberInfos;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
