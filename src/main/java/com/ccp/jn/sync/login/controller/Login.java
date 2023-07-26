package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;

public class Login{

	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnBusinessEntity.password)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnBusinessEntity.password_tries)
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
		.toBeginProcedureAnd()
			.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()
			.loadThisIdFromTable(JnBusinessEntity.password_tries).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.password).returnStatus(202).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.password).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById.content;
	}
}

