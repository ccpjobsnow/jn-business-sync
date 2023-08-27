package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;

public class SavePreRegistration {

	public void execute (CcpMapDecorator values){
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnEntity.pre_registration.createOrUpdate(valores);
		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).executeAction(action).andFinally()
		.endThisProcedure()
		;

	
	}
}
