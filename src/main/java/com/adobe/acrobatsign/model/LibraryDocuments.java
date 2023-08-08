package com.adobe.acrobatsign.model;

import java.util.List;

public class LibraryDocuments {
	
	private List<LibraryDocument> libraryDocumentList;
	private PageInfo page;
	public List<LibraryDocument> getLibraryDocumentList() {
		return libraryDocumentList;
	}
	public void setLibraryDocumentList(List<LibraryDocument> libraryDocumentList) {
		this.libraryDocumentList = libraryDocumentList;
	}
	public PageInfo getPage() {
		return page;
	}
	public void setPage(PageInfo page) {
		this.page = page;
	}

}
