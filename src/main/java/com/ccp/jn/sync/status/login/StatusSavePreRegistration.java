package com.ccp.jn.sync.status.login;

public enum StatusSavePreRegistration implements StatusEndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	tokenFaltando(404),
	lockedPassword(421),
	loginConflict(409),
	missingPassword(202),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusSavePreRegistration(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
