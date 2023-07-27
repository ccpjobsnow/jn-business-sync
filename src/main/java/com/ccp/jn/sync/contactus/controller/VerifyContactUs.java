package com.ccp.jn.sync.contactus.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.jn.commons.JnEntity;

public class VerifyContactUs {
	
	@CcpDependencyInject
	private CcpDao crud;

	public void execute(String sender, String subjectType){
		
		CcpMapDecorator values = new CcpMapDecorator().put("sender", sender).put("subjectType", subjectType);

		//this.crud.findById(values, new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.contact_us).put("status", 200));

		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnEntity.contact_us).returnStatus(200).andFinally()
		.endThisProcedure();
	}
}
