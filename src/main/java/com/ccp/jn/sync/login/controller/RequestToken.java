package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestToken {

	@CcpDependencyInject
	private CcpDao dao;

	
	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		CcpProcess action = valores -> JnTopic.sendUserToken.send(valores);
		CcpMapDecorator result = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		return result;
	}
}
