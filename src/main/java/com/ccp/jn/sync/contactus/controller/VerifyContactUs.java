package com.ccp.jn.sync.contactus.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;

public class VerifyContactUs {
	
	@CcpDependencyInject
	private CcpDao dao;

	public void execute(String sender, String subjectType){
		
		CcpMapDecorator values = new CcpMapDecorator().put("sender", sender).put("subjectType", subjectType);

		this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.contact_us).returnStatus(JnProcessStatus.nextStep).andFinally()
		.endThisProcedure();
	}
}
