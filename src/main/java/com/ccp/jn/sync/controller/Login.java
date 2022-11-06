package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.business.login.EvaluateLoginTries;
import com.ccp.jn.sync.business.login.SaveLogin;
import com.ccp.jn.sync.business.login.ValidateLogin;
import com.ccp.jn.sync.business.login.LockLogin;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class Login{

	@CcpEspecification
	private CcpPasswordHandler bcript;

	private CcpProcess decisionTree = values ->{
		
		return new ValidateLogin(this.bcript)
				.addStep(200, new SaveLogin())
				.addStep(401, new EvaluateLoginTries()
						.addStep(429, new LockLogin())
				)
				.goToTheNextStep(values).data;
		
	};

	@CcpEspecification
	private CcpDbCrud crud;
	
	public Map<String, Object> execute(CcpMapDecorator values) {

		CcpMapDecorator findById = this.crud.findById(values, 
				    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
         		   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.password).put("status", 202)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.password).put("action", this.decisionTree)
				);
		return findById.content;
	}
}

