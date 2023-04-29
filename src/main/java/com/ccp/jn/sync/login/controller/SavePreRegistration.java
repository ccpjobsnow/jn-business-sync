package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.jn.commons.JnBusinessEntity;

public class SavePreRegistration {

	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public void execute (CcpMapDecorator values){
		
		this.crud.findById(values,  
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("status", 404)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("action", valores -> JnBusinessEntity.pre_registration.save(valores))
			);
	}
}
