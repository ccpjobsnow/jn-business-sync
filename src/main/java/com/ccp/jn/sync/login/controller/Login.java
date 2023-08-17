package com.ccp.jn.sync.login.controller;

import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

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
	
	
	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnEntity.password)
				.addStep(Status.nextStep, new ResetEntity("tries", 3, JnEntity.password_tries)
						.addStep(Status.nextStep, new SaveLogin())
						)
				.addStep(Status.wrongPassword, new EvaluateTries(JnEntity.password_tries, Status.wrongPassword, Status.exceededTries)
						.addStep(Status.exceededTries, JnEntity.locked_password.getSaver(Status.exceededTries))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDao dao;
	
	public CcpMapDecorator execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.loadThisIdFromEntity(JnEntity.password_tries).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.alreadyLogged).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.password).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
}

