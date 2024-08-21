package com.adobe.acrobatsign.entity;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "PARTICIPANT_DETAILS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantList implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<String> role;
    public String fullName;
    public String participantSetNames;
    public String company;
    @Id
    public String email;
	public ArrayList<String> getRole() {
		return role;
	}
	public void setRole(ArrayList<String> role) {
		this.role = role;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getParticipantSetNames() {
		return participantSetNames;
	}
	public void setParticipantSetNames(String participantSetNames) {
		this.participantSetNames = participantSetNames;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public ParticipantList() {}

}
