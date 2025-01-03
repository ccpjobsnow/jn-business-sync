package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum StatusExecuteLogout implements CcpProcessStatus{
	invalidEmail(400),
	missingLogin(404),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusExecuteLogout(int status) {
		this.status = status;
	}

	public int asNumber() {
		return status;
	}
}
