package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.jn.commons.JnBusinessEntity;

public class VerifyContactUs {
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById = this.crud.findById(values
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.contact_us).put("status", 200)
				);
		
		return findById.content;
	}
}
