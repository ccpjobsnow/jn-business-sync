package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnEntity;

public class ExistsLoginToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDao crud;

	
	public Map<String, Object> execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnEntity.password).returnStatus(202).andFinally()
		.endThisProcedure()
		;
	
		
		return values.content;
	}
}
