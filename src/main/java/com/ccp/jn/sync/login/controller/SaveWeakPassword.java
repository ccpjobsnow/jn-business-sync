package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.SaveLogin;
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
			 SaveLogin saveLogin = new SaveLogin();
			 CcpNextStep savePasswordAndExecuteLogin = this.passwordHandler.addNextStep(saveLogin);
			 CcpNextStep saveWeakPassword = JnEntity.weak_password.getSaver().addNextStep(savePasswordAndExecuteLogin);
			 CcpNextStep addStep = new EvaluatePasswordStrength().addStep(Status.weakPassword, saveWeakPassword);
			 CcpStepResult goToTheNextStep = addStep.goToTheNextStep(values);
			 return goToTheNextStep.values;
		 };
		 
		CcpMapDecorator values = new CalculateId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats).andSo()	
				.ifThisIdIsNotPresentInEntity(JnEntity.password).executeAction(decisionTree).andFinally()
			.endThisProcedureRetrievingTheResultingData();
		 return values;
		
	}
}
