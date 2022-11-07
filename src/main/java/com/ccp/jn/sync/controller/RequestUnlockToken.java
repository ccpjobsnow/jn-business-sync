package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestUnlockToken {
	
	@CcpEspecification
	private CcpDbCrud crud;
	
	@CcpEspecification
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute(CcpMapDecorator values) {
		
		this.crud.findById(values,  
				 new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 422)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token).put("status", 420)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token_answered).put("status", 204)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.failed_unlock_token_today).put("status", 403)
   			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
   			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.password).put("status", 202)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_unlock_token)
			    .put("action", valores -> this.mensageriaSender.send(values, JnBusinessTopic.RequestUnlockToken))
			);

		return values.content;
	}
}