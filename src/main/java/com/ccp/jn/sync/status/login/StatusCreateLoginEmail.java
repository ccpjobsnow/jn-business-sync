package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum StatusCreateLoginEmail implements CcpProcessStatus{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	lockedPassword(421),
	loginConflict(409),
	missingSavePassword(202),
	missingSaveAnswers(201),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusCreateLoginEmail(int status) {
		this.status = status;
	}

	public int asNumber() {
		return status;
	}
}
