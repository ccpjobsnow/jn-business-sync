package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;
public class UpdatePassword {
	private static enum Status implements CcpProcessStatus{
		wrongToken(401),
		correctToken(200),
		exceededTries(403),
		weakPassword(422)
		
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

	@CcpDependencyInject
	private CcpDao dao;

	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		CcpNextStep savePassword = new SavePassword().addStep(Status.nextStep, new SaveLogin());
		CcpNextStep saveWeakPassword = JnEntity.weak_password.getSaver(Status.nextStep).addStep(Status.nextStep, new SaveLogin());
		CcpNextStep evaluatePasswordStrength = new EvaluatePasswordStrength().addStep(Status.weakPassword, saveWeakPassword).addStep(Status.nextStep, savePassword);
		CcpNextStep unLockPassword = new TransferDataBetweenEntities(JnEntity.locked_password, JnEntity.unlocked_password).addStep(Status.nextStep, evaluatePasswordStrength);
		CcpNextStep solveLoginConflict = new TransferDataBetweenEntities(JnEntity.login_conflict, JnEntity.login_conflict_solved).addStep(Status.nextStep, unLockPassword);
		CcpNextStep removeTokenTries = new ResetEntity("tries", 3, JnEntity.token_tries).addStep(Status.nextStep, solveLoginConflict);
		CcpNextStep evaluateTokenTries = new EvaluateTries(JnEntity.token_tries, Status.wrongToken, Status.exceededTries).addStep(Status.exceededTries, JnEntity.locked_token.getSaver(Status.exceededTries));
		CcpNextStep evaluateToken = new EvaluateToken().addStep(Status.wrongToken, evaluateTokenTries).addStep(Status.correctToken, removeTokenTries);
		
		CcpStepResult goToTheNextStep = evaluateToken.goToTheNextStep(values);
		return goToTheNextStep.values;
		
	};

	
	public CcpMapDecorator execute (CcpMapDecorator values){
		
		/*
		 * Salvar senha desbloqueada
		 */
		CcpMapDecorator result = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).executeAction(this.decisionTree).andFinally()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
}
