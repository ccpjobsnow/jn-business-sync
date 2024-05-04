package com.ccp.jn.sync.status.login;

public enum StatusCreateLoginToken implements StatusEndpointsLogin{
	statusInvalidEmail(400),
	statusLockedToken(403),
	statusMissingEmail(404),
	missingAnswers(201),
	statusExpectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusCreateLoginToken(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
