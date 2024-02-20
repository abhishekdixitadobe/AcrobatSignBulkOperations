package com.adobe.acrobatsign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LibraryDocuments {

	private List<LibraryDocument> libraryDocumentList;
	private PageInfo page;

	public List<LibraryDocument> getLibraryDocumentList() {
		return libraryDocumentList;
	}

	public PageInfo getPage() {
		return page;
	}

	public void setLibraryDocumentList(List<LibraryDocument> libraryDocumentList) {
		this.libraryDocumentList = libraryDocumentList;
	}

	public void setPage(PageInfo page) {
		this.page = page;
	}

}
