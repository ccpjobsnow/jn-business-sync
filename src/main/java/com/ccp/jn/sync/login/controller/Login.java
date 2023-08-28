package com.ccp.jn.sync.login.controller;

import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.CreateLogin;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;
import com.jn.commons.SaveEntity;

public class Login{
	
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
	
	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values -> {
		
		SaveEntity lockPassword = JnEntity.locked_password.getSaver(Status.exceededTries);
		CcpNextStep executeLogin = new ResetEntity("tries", 3, JnEntity.password_tries).addMostExpectedStep(new CreateLogin());

		CcpNextStep evaluateTries = new EvaluateTries(JnEntity.password_tries, Status.wrongPassword, Status.exceededTries)
				.addAlternativeStep(Status.exceededTries, lockPassword);
		
		CcpNextStep validatePassword = new ValidatePassword(JnEntity.password)
				.addAlternativeStep(Status.wrongPassword, evaluateTries)
				.addMostExpectedStep(executeLogin);
		
		return validatePassword.goToTheNextStep(values).values;
	};

	public CcpMapDecorator execute(Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById =  new CalculateId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.password).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
}

