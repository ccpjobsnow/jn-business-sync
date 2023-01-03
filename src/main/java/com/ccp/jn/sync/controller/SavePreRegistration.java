package com.ccp.jn.backend.site.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.jn.commons.JnBusinessEntity;

public class SavePreRegistration {

	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		this.crud.findById(values,  
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("action", valores -> JnBusinessEntity.pre_registration.save(valores))
			);

		return values.content;
	}
}
