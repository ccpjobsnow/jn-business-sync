package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestToken {

	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnTopic.sendUserToken.send(valores);
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		return result;
	}
}
