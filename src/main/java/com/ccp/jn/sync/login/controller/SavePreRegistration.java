package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class SavePreRegistration {

	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public void execute (CcpMapDecorator values){
		
		CcpProcess action = valores -> JnBusinessEntity.pre_registration.save(valores);
		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).thenReturnStatus(401).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).thenReturnStatus(409).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenDoAnAction(action).andFinally()
		.endThisProcedure()
		;

	
	}
}
