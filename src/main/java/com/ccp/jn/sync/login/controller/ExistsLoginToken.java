package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;

public class ExistsLoginToken {

	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.alreadyLogged).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).andFinally()
		.endThisProcedure()
		;
	}
}
