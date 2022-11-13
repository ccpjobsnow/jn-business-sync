package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.table.ResetTable;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.business.commons.password.ValidatePassword;
import com.ccp.jn.sync.business.commons.tries.EvaluateTries;
import com.ccp.jn.sync.business.login.LockLogin;
import com.ccp.jn.sync.business.login.SaveLogin;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class Login{

	@CcpEspecification
	private CcpPasswordHandler passwordHandler;

	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnBusinessEntity.password)
				.addStep(200, new ResetTable(JnBusinessEntity.password_tries)
						.addStep(200, new SaveLogin())
						)
				.addStep(401, new EvaluateTries(JnBusinessEntity.password_tries, 401, 429)
						.addStep(429, new LockLogin())
				)
				.goToTheNextStep(values).data;
		
	};

	@CcpEspecification
	private CcpDbCrud crud;
	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById = this.crud.findById(values, 
				    new CcpMapDecorator().put("table", JnBusinessEntity.user_stats)
    			   ,new CcpMapDecorator().put("table", JnBusinessEntity.password_tries)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
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

