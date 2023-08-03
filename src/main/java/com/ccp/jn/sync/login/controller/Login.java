package com.ccp.jn.sync.login.controller;

import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class Login{

	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnEntity.password)
				.addStep(200, new ResetEntity("tries", 3, JnEntity.password_tries)
						.addStep(200, new SaveLogin())
						)
				.addStep(401, new EvaluateTries(JnEntity.password_tries, 401, 429)
						.addStep(429, JnEntity.locked_password.getSaver(429))
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
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(202).and()
			.ifThisIdIsPresentInEntity(JnEntity.password).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
}

