package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum StatusUpdatePassword implements CcpProcessStatus{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	missingToken(404),
	wrongToken(421),
	tokenLockedRecently(429),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusUpdatePassword(int status) {
		this.status = status;
	}

	public int asNumber() {
		return status;
	}
}
