package com.adobe.acrobatsign.model;

public class AccessTokenResponse {
	private String access_token;
	private String refresh_token;
	private String api_access_point;
	private String web_access_point;
	private String token_type;
	private String expires_in;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getApi_access_point() {
		return api_access_point;
	}
	public void setApi_access_point(String api_access_point) {
		this.api_access_point = api_access_point;
	}
	public String getWeb_access_point() {
		return web_access_point;
	}
	public void setWeb_access_point(String web_access_point) {
		this.web_access_point = web_access_point;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

}
;
