package com.adobe.acrobatsign.util;

public enum Roles {
	Approver("APPROVER"), Signer("SIGNER");
    private String value;

    Roles(String value) {
    	this.value = value;
	}

	public String getResponse() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    public static Roles fromString(String text) {
        for (Roles b : Roles.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
