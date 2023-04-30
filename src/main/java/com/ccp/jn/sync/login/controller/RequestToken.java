package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.sendUserToken);
		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenDoAnAction(action).andFinally()
		.endThisProcedure()
		;

		
		return values.content;
	}
}
