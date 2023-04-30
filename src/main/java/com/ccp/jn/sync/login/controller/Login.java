package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.ResetTable;

public class Login{

	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnBusinessEntity.password)
				.addStep(200, new ResetTable(JnBusinessEntity.password_tries)
						.addStep(200, new SaveLogin())
						)
				.addStep(401, new EvaluateTries(JnBusinessEntity.password_tries, 401, 429)
						.addStep(429, JnBusinessEntity.locked_password.getSaver(429))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDbCrud crud;
	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById = this.crud
		.useThisId(values)
		.toBeginProcedure()
			.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()
			.loadThisIdFromTable(JnBusinessEntity.password_tries).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).thenReturnStatus(401).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).thenReturnStatus(409).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenReturnStatus(201).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).thenReturnStatus(202).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.password).thenDoAnAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById.content;
	}
}

