package com.ccp.jn.sync.status.login;

public enum StatusCreateLoginEmail implements StatusEndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	lockedPassword(401),
	loginConflict(409),
	missingPassword(202),
	missingAnswers(201),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusCreateLoginEmail(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
