package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.RequestTokenAction;
import com.jn.commons.JnBusinessEntity;

public class RequestToken {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		this.crud.findById(values,  
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("action", new RequestTokenAction(this.mensageriaSender))
			);

		return values.content;
	}
}
