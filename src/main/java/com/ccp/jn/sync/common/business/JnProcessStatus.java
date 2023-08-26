package com.ccp.jn.sync.common.business;

import com.ccp.process.CcpProcessStatus;

public enum JnProcessStatus implements CcpProcessStatus {
	loginTokenIsLocked(403),
	passwordIsLocked(401),
	alreadyLogged(409),
	preRegistrationIsMissing(201),
	passwordIsMissing(202),
	loginTokenIsMissing(404),
	candidateNotFound(404),
	resumeNotFound(404),
	resumeHasBeenDeniedToRecruiter(403),
	tokenAlreadyRequested(420),
	tokenAlreadySent(204),
	unableToExecuteLogout(404),
	unableToRequestUnLockToken(422),
	unlockTokenAlreadyRequested(420),
	unlockTokenAlreadyAnswered(204),
	unlockTokenHasFailed(403),
	unableToUnlockToken(404),
	tokenIsNotLocked(422),
	unlockTokenHasNotBeenRequested(420)
	
	;
	int status;

	private JnProcessStatus(int status) {
		this.status = status;
	}

	public int status() {
		return this.status;
	}

}