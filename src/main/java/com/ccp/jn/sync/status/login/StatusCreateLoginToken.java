package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum StatusCreateLoginToken implements CcpProcessStatus{
	statusInvalidEmail(400),
	statusLockedToken(403),
	statusMissingEmail(404),
	missingSaveAnswers(201),
	expectedStatus(200),
	statusAlreadySentToken(429)
	;

	public final int status;
	
	
	
	private StatusCreateLoginToken(int status) {
		this.status = status;
	}

	public int asNumber() {
		return status;
	}
}
