package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.SavePassword;
import com.ccp.jn.sync.business.SaveWeakPasswordAction;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class SaveWeakPassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	public void execute (CcpMapDecorator parameters){

		CcpProcess saveWeakPassword = valores -> new SaveWeakPasswordAction()
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin()))
				.addStep(201, null)
				.goToTheNextStep(valores).values;
		
			this.crud.findById(parameters,  
			        new CcpMapDecorator().put("table", JnBusinessEntity.user_stats)
			       ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("status", 404)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.weak_password).put("action", saveWeakPassword)
				);
		
	}
}
