package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class CreateLoginToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.sendUserToken);

		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.login_token).executeAction(action).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.password).returnStatus(202).andFinally()
		.endThisProcedure()
		;

	}
}
