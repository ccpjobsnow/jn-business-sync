package com.ccp.jn.sync.contactus.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.UseThisId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;

public class VerifyContactUs {
	
	public void execute(String sender, String subjectType){
		
		CcpMapDecorator values = new CcpMapDecorator().put("sender", sender).put("subjectType", subjectType);
		 new UseThisId(values, new CcpMapDecorator())
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.contact_us).returnStatus(JnProcessStatus.nextStep).andFinally()
		.endThisProcedure();
	}
}
