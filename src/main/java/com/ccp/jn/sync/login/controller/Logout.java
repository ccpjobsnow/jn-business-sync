package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class Logout {
	
	@CcpDependencyInject
	private CcpDbCrud crud;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		CcpProcess action = x -> new TransferDataBetweenTables(JnBusinessEntity.login, JnBusinessEntity.logout).goToTheNextStep(x).values;
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.login).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).executeAction(action).andFinally()
		.endThisProcedure()
		;

		
	}
}
