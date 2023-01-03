package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestUnlockToken {
	
	@CcpDependencyInject
	private CcpDbCrud crud;
	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		this.crud.findById(values,  
				 new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.locked_token).put("status", 422)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token).put("status", 420)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token_answered).put("status", 204)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.failed_unlock_token).put("status", 403)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_unlock_token)
			    .put("action", valores -> this.mensageriaSender.send(values, JnBusinessTopic.requestUnlockToken))
			);

		return values.content;
	}
}
