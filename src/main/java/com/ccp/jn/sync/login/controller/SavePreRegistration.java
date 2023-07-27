package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class SavePreRegistration {

	@CcpDependencyInject
	private CcpDao crud;

	
	public void execute (CcpMapDecorator values){
		
		CcpProcess action = valores -> JnEntity.pre_registration.createOrUpdate(valores);
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).executeAction(action).andFinally()
		.endThisProcedure()
		;

	
	}
}
