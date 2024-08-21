package com.adobe.acrobatsign.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "AGREEMENT_DETAILS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementDetail implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		@Id
    	public String id;
	 	public ArrayList<String> role;
	    public String hidden;
	    public String groupId;
	    public String externalId;
	    public String subTypes;
	    public String type;
	    public String userId;
	    public String parentId;
	    @Column(name = "participantList", columnDefinition = "NVARCHAR(MAX)")
	    public ArrayList<ParticipantList> participantList;
	    public Date createdDate;
	    public Date modifiedDate;
	    public String name;
	    public String workflowId;
	    public String status;
	    public Date expirationDate;
		public ArrayList<String> getRole() {
			return role;
		}
		public void setRole(ArrayList<String> role) {
			this.role = role;
		}
		public String getHidden() {
			return hidden;
		}
		public void setHidden(String hidden) {
			this.hidden = hidden;
		}
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getExternalId() {
			return externalId;
		}
		public void setExternalId(String externalId) {
			this.externalId = externalId;
		}
		public String getSubTypes() {
			return subTypes;
		}
		public void setSubTypes(String subTypes) {
			this.subTypes = subTypes;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public ArrayList<ParticipantList> getParticipantList() {
			return participantList;
		}
		public void setParticipantList(ArrayList<ParticipantList> participantList) {
			this.participantList = participantList;
		}
		public Date getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}
		public Date getModifiedDate() {
			return modifiedDate;
		}
		public void setModifiedDate(Date modifiedDate) {
			this.modifiedDate = modifiedDate;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getWorkflowId() {
			return workflowId;
		}
		public void setWorkflowId(String workflowId) {
			this.workflowId = workflowId;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Date getExpirationDate() {
			return expirationDate;
		}
		public void setExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
		}
		
		public AgreementDetail() {}
	
}
