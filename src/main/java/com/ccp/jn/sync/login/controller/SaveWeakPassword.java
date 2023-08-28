package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.CreateLogin;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpProcessStatus;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {
	private static enum Status implements CcpProcessStatus{
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

	private final SavePassword passwordHandler = new SavePassword();

	public CcpMapDecorator execute (CcpMapDecorator parameters){
		
		 Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values -> {
			 
			 CreateLogin saveLogin = new CreateLogin();
			 CcpNextStep createPasswordAndExecuteLogin = this.passwordHandler.addMostExpectedStep(saveLogin);
			 CcpNextStep createWeakPassword = JnEntity.weak_password.getSaver().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep deleteWeakPasswordIfExists = JnEntity.weak_password.getDeleter().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep evaluatePasswordStrength = new EvaluatePasswordStrength().addMostExpectedStep(deleteWeakPasswordIfExists).addAlternativeStep(Status.weakPassword, createWeakPassword);
			 CcpStepResult goToTheNextStep = evaluatePasswordStrength.goToTheNextStep(values);
			 
			 return goToTheNextStep.values;
		 };
		 
		CcpMapDecorator values = new CalculateId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats)
				.andSo()	
					.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
					.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
					.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
					.executeAction(decisionTree)
				.andFinally()
			.endThisProcedureRetrievingTheResultingData();
		 
		return values;
		
	}
}
