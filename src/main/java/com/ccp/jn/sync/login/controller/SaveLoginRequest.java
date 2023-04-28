package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.RequestToken;
import com.jn.commons.JnBusinessEntity;

public class SaveLoginRequest {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		this.crud.findById(values,  
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("action", new RequestToken(this.mensageriaSender))
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.password).put("status", 202)
			);

		return values.content;
	}
}