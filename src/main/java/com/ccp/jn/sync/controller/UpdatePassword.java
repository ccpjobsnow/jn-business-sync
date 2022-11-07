package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.commons.EvaluateTries;
import com.ccp.jn.sync.business.login.SaveLogin;
import com.ccp.jn.sync.business.password.EvaluatePasswordStrength;
import com.ccp.jn.sync.business.password.EvaluateToken;
import com.ccp.jn.sync.business.password.LockToken;
import com.ccp.jn.sync.business.password.ResetTokenTries;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class UpdatePassword {

	@CcpEspecification
	private CcpMensageriaSender mensageriaSender;
	
	@CcpEspecification
	private CcpDbCrud crud;

	
	private CcpProcess decisionTree = values ->{
		
		return new EvaluateToken()
				.addStep(401, new EvaluateTries(JnBusinessEntity.token_tries, 401, 403)
							.addStep(403, new LockToken())
						)
				.addStep(200, new ResetTokenTries()
							.addStep(200, new EvaluatePasswordStrength()
										.addStep(200, new SaveLogin())
									)	
						)
				.goToTheNextStep(values).data;
		
	};

	
	public Map<String, Object> execute(CcpMapDecorator values) {
		
		this.crud.findById(values,  
				    new CcpMapDecorator().put("table", JnBusinessEntity.user_stats)
				   ,new CcpMapDecorator().put("table", JnBusinessEntity.token_tries)
    			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.weak_password).put("action", this.decisionTree)
				);
		
		
		return values.content;
	}
}
