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
//
//		this.crud.findById(values,  
//				 new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("status", 404)
//			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.locked_token).put("status", 422)
//			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token).put("status", 420)
//			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token_answered).put("status", 204)
//			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.failed_unlock_token).put("status", 403)
//			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_unlock_token)
//			    .put("action", valores -> this.mensageriaSender.send(values, JnBusinessTopic.requestUnlockToken))
//			);
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.requestUnlockToken);
		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(422).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token).thenReturnStatus(420).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token_answered).thenReturnStatus(204).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.failed_unlock_token).thenReturnStatus(403).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.request_unlock_token).thenDoAnAction(action).andFinally()
		.endThisProcedure()
		;

	}
}
