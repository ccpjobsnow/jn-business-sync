package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class CreateLoginToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDao crud;

	
	public void execute (String email, String language){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("language", language);
		
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnTopic.sendUserToken);

		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).executeAction(action).and()
			.ifThisIdIsPresentInTable(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnEntity.password).returnStatus(202).andFinally()
		.endThisProcedure()
		;

	}
}
