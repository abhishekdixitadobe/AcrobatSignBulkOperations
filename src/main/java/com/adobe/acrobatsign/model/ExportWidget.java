package com.adobe.acrobatsign.model;

public class ExportWidget {

	private String id;
	private String name;
	private String status;
	private String url;
	private String ownerEmail;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public String getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
