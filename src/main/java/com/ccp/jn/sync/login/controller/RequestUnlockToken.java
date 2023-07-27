package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken {
	
	@CcpDependencyInject
	private CcpDao crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnTopic.requestUnlockToken);
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInTable(JnEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsPresentInTable(JnEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInTable(JnEntity.request_unlock_token_answered).returnStatus(204).and()
			.ifThisIdIsNotPresentInTable(JnEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnEntity.request_unlock_token).executeAction(action).andFinally()
		.endThisProcedure()
		;

	}
}
