package com.ccp.jn.backend.site.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.SavePassword;
import com.ccp.jn.sync.business.SaveWeakPassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class SaveBadPassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	private CcpProcess decisionTree = values -> {
		
		return new SaveWeakPassword()
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin())
						)
				
				.goToTheNextStep(values).data;
		
	};

	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		this.crud.findById(values,  
			        new CcpMapDecorator().put("table", JnBusinessEntity.user_stats)
			       ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.weak_password).put("action", this.decisionTree)
				);
		
		
		return values.content;
	}
}
