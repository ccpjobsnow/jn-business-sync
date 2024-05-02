package com.ccp.jn.sync.status.login;

public enum CreateLoginToken implements EndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	missingAnswers(201),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private CreateLoginToken(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
