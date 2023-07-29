package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.jn.commons.JnEntity;

public class ExistsLoginToken {

	@CcpDependencyInject
	private CcpDao crud;

	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(202).andFinally()
		.endThisProcedure()
		;
	}
}
