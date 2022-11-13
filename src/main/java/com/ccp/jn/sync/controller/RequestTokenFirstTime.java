package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.token.request.RequestToken;
import com.jn.commons.JnBusinessEntity;

public class RequestTokenFirstTime {

	@CcpEspecification
	private CcpMensageriaSender mensageriaSender;
	
	@CcpEspecification
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		this.crud.findById(values,  
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("action", new RequestToken(this.mensageriaSender))
			);

		return values.content;
	}
}