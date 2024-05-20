package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum StatusSaveAnswers implements CcpProcessStatus{
	invalidEmail(400),
	lockedToken(403),
	tokenFaltando(404),
	lockedPassword(421),
	loginConflict(409),
	missingPassword(202),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusSaveAnswers(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
