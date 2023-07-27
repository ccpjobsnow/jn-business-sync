package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class Login{

	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnEntity.password)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnEntity.password_tries)
						.addStep(200, new SaveLogin())
						)
				.addStep(401, new EvaluateTries(JnEntity.password_tries, 401, 429)
						.addStep(429, JnEntity.locked_password.getSaver(429))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDao crud;
	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById = this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromTable(JnEntity.user_stats).andSo()
			.loadThisIdFromTable(JnEntity.password_tries).andSo()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsPresentInTable(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnEntity.password).returnStatus(202).and()
			.ifThisIdIsPresentInTable(JnEntity.password).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById.content;
	}
}

