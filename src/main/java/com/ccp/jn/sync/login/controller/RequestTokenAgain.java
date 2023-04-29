package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestTokenAgain {
	
	@CcpDependencyInject
	private CcpDbCrud crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);

		this.crud.findById(values,  
			     new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("status", 404)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_token_again).put("status", 420)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_token_again_answered).put("status", 204)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_token_again)
			    .put("action", valores -> this.mensageriaSender.send(values, JnBusinessTopic.requestTokenAgain))
			);

		return values.content;
	}
}
