package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestTokenAgain {
	
	@CcpDependencyInject
	private CcpDbCrud crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);

		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.requestTokenAgain);
	
		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_token_again).thenReturnStatus(420).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_token_again_answered).thenReturnStatus(204).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.request_token_again).thenDoAnAction(action).andFinally()
		.endThisProcedure()
		;
		
	}
}
