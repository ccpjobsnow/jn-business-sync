package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestUnlockToken {
	
	@CcpDependencyInject
	private CcpDbCrud crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.requestUnlockToken);
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token_answered).returnStatus(204).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.request_unlock_token).executeAction(action).andFinally()
		.endThisProcedure()
		;

	}
}
