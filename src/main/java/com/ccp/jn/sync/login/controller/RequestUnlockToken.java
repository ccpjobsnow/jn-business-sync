package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken {
	
	@CcpDependencyInject
	private CcpDao crud;
	
	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		CcpProcess action = valores -> JnTopic.requestUnlockToken.send(valores);
		CcpMapDecorator result = this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).returnStatus(204).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
