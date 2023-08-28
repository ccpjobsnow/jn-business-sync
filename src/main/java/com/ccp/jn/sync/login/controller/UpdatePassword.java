package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.CreateLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpProcessStatus;
import com.ccp.process.CcpStepResult;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;
public class UpdatePassword {
	private static enum Status implements CcpProcessStatus{
		wrongToken(401),
		correctToken(200),
		exceededTries(403),
		
		;
		int status;
		private Status(int status) {
			this.status = status;
		}
		@Override
		public int status() {
			return this.status;
		}
		
	}


	private final SavePassword passwordHandler = new SavePassword();
	
	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		CcpNextStep savePassword = this.passwordHandler.addMostExpectedStep(new CreateLogin());
		CcpNextStep evaluatePasswordStrength = new EvaluatePasswordStrength().addMostExpectedStep(savePassword);
		CcpNextStep unLockPassword = new TransferDataBetweenEntities(JnEntity.locked_password, JnEntity.unlocked_password).addMostExpectedStep(evaluatePasswordStrength);
		CcpNextStep solveLoginConflict = new TransferDataBetweenEntities(JnEntity.login_conflict, JnEntity.login_conflict_solved).addMostExpectedStep(unLockPassword);
		CcpNextStep removeTokenTries = new ResetEntity("tries", 3, JnEntity.token_tries).addMostExpectedStep(solveLoginConflict);
		CcpNextStep evaluateTokenTries = new EvaluateTries(JnEntity.token_tries, Status.wrongToken, Status.exceededTries).addAlternativeStep(Status.exceededTries, JnEntity.locked_token.getSaver(Status.exceededTries));
		CcpNextStep evaluateToken = new EvaluateToken().addAlternativeStep(Status.wrongToken, evaluateTokenTries).addAlternativeStep(Status.correctToken, removeTokenTries);
		
		CcpStepResult goToTheNextStep = evaluateToken.goToTheNextStep(values);
		return goToTheNextStep.values;
		
	};

	
	public CcpMapDecorator execute (CcpMapDecorator values){
		
		/*
		 *TODO Salvar senha desbloqueada
		 */
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.executeAction(this.decisionTree).andFinally()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
}
