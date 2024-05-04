package com.ccp.jn.sync.status.login;

public enum StatusExecuteLogout implements StatusEndpointsLogin{
	invalidEmail(400),
	missingLogin(404),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusExecuteLogout(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
