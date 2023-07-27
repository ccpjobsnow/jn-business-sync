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
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).returnStatus(202).andFinally()
		.endThisProcedure()
		;
	
		
		return values.content;
	}
}
