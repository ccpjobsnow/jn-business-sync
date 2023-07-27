package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestTokenAgain {
	
	@CcpDependencyInject
	private CcpDao crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);

		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnTopic.requestTokenAgain);
	
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.request_token_again).returnStatus(420).and()
			.ifThisIdIsPresentInTable(JnEntity.request_token_again_answered).returnStatus(204).and()
			.ifThisIdIsNotPresentInTable(JnEntity.request_token_again).executeAction(action).andFinally()
		.endThisProcedure()
		;
		
	}
}
