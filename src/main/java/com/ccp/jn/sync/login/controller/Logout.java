package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;

public class Logout {
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = x -> new TransferDataBetweenEntities(JnEntity.login, JnEntity.logout).goToTheNextStep(x).values;
		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.unableToExecuteLogout).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).executeAction(action).andFinally()
		.endThisProcedure()
		;

		
	}
}
