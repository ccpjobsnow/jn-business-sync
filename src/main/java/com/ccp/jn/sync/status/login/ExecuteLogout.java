package com.ccp.jn.sync.status.login;

public enum ExecuteLogout implements EndpointsLogin{
	invalidEmail(400),
	missingLogin(404),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private ExecuteLogout(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
