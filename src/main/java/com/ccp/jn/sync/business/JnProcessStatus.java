package com.ccp.jn.sync.business;

import com.ccp.process.CcpProcessStatus;

public enum JnProcessStatus implements CcpProcessStatus {
	loginTokenIsLocked(403),
	passwordIsLocked(401),
	loginInUse(409),
	preRegistrationIsMissing(201),
	passwordIsMissing(202),
	loginTokenIsMissing(404),
	candidateNotFound(404),
	resumeNotFound(404),
	resumeHasBeenDeniedToRecruiter(403),
	tokenAlreadyRequested(409),
	tokenAlreadySent(204),
	unableToExecuteLogout(404),
	unableToRequestUnLockToken(422),
	unlockTokenAlreadyRequested(420),
	unlockTokenAlreadyAnswered(409),
	unlockTokenHasFailed(421),
	unableToUnlockToken(404),
	tokenIsNotLocked(422),
	wrongToken(401),
	weakPassword(422),
	exceededTries(429),
	wrongPassword(422),
	requestToUnlockDoesNotExist(422), 
	thisUserIsNotAllowedToDoSupport(401), 
	requestAlreadyAnswered(409),
	waitingForSupport(202)
	
	;
	int status;

	private JnProcessStatus(int status) {
		this.status = status;
	}

	public int status() {
		return this.status;
	}

}
