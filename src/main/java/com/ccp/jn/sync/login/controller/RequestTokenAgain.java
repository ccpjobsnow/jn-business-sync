package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestTokenAgain {
	
	@CcpDependencyInject
	private CcpDao crud;

	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);

		CcpProcess action = valores -> JnTopic.requestTokenAgain.send(valores);
	
		CcpMapDecorator result = this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_token_again).returnStatus(420).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_token_again_answered).returnStatus(204).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_token_again).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
}
