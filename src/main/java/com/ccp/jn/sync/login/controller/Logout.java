package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class Logout {
	
	@CcpDependencyInject
	private CcpDao crud;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		CcpProcess action = x -> new TransferDataBetweenTables(JnEntity.login, JnEntity.logout).goToTheNextStep(x).values;
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTable(JnEntity.login).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.login).executeAction(action).andFinally()
		.endThisProcedure()
		;

		
	}
}
