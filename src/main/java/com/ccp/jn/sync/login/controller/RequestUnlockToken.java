package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken {
	
	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnTopic.requestUnlockToken.send(valores);
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.unableToRequestUnLockToken).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).returnStatus(JnProcessStatus.unlockTokenAlreadyAnswered).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.failed_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
