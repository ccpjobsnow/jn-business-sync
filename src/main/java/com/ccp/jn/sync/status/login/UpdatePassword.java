package com.ccp.jn.sync.status.login;

public enum UpdatePassword implements EndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	wrongToken(401),
	tokenLockedRecently(429),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private UpdatePassword(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
