package com.ccp.jn.sync.contactus.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.jn.commons.JnBusinessEntity;

public class VerifyContactUs {
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	public void execute (String emailFrom, String subjectType){
		
		CcpMapDecorator values = new CcpMapDecorator().put("emailFrom", emailFrom).put("subjectType", subjectType);

		this.crud.findById(values, new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.contact_us).put("status", 200));
		
	}
}