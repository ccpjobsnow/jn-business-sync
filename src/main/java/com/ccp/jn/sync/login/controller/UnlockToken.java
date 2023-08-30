package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class UnlockToken {
	private static enum Status implements CcpProcessStatus{
		wrongPassword(401),
		exceededTries(429)
		;
		int status;

		private Status(int status) {
			this.status = status;
		}

		public int status() {
			return this.status;
		}
	}


	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		return new ValidatePassword(JnEntity.request_unlock_token_answered)
				.addMostExpectedStep(new ResetEntity("tries", 3, JnEntity.unlock_token_tries)
						.addMostExpectedStep(new TransferDataBetweenEntities(JnEntity.locked_token, JnEntity.unlocked_token)
							)
						)
				.addAlternativeStep(Status.wrongPassword, new EvaluateTries(JnEntity.unlock_token_tries, Status.wrongPassword, Status.exceededTries)
						.addAlternativeStep(Status.exceededTries, JnEntity.locked_password.getSaver(Status.exceededTries))
				)
				.goToTheNextStep(values).values;
		
	};

	public CcpMapDecorator execute (CcpMapDecorator parameters){
		
		CcpMapDecorator result = new CalculateId(parameters)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.unableToUnlockToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.tokenIsNotLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasNotBeenRequested).and()
			.ifThisIdIsPresentInEntity(JnEntity.failed_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
}
