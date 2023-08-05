package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.jn.commons.JnEntity;

public class SavePreRegistration {

	@CcpDependencyInject
	private CcpDao dao;

	
	public void execute (CcpMapDecorator values){
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnEntity.pre_registration.createOrUpdate(valores);
		this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).executeAction(action).andFinally()
		.endThisProcedure()
		;

	
	}
}
