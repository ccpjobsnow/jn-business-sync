package com.ccp.jn.sync.status.login;

public enum StatusUpdatePassword implements StatusEndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	wrongToken(421),
	tokenLockedRecently(429),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusUpdatePassword(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
