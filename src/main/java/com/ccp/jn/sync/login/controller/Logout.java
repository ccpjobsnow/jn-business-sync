package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class Logout {
	
	@CcpDependencyInject
	private CcpDao dao;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		CcpProcess action = x -> new TransferDataBetweenEntities(JnEntity.login, JnEntity.logout).goToTheNextStep(x).values;
		this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).executeAction(action).andFinally()
		.endThisProcedure()
		;

		
	}
}
