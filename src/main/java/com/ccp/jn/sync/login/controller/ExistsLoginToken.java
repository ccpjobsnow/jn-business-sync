package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;

public class ExistsLoginToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).thenReturnStatus(401).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).thenReturnStatus(409).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenReturnStatus(201).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).thenReturnStatus(202).andFinally()
		.endThisProcedure()
		;
	
		
		return values.content;
	}
}
