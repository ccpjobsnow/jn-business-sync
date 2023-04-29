package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.business.LockLogin;
import com.ccp.jn.sync.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.ResetTable;

public class UnlockToken {
	
	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnBusinessEntity.request_unlock_token_answered)
				.addStep(200, new ResetTable(JnBusinessEntity.unlock_token_tries)
						.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.locked_token, JnBusinessEntity.unlocked_token)
							)
						)
				.addStep(401, new EvaluateTries(JnBusinessEntity.unlock_token_tries, 401, 429)
						.addStep(429, new LockLogin())
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDbCrud crud;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		this.crud.findById(values,  
				 new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_token).put("status", 404)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.locked_token).put("status", 422)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_unlock_token).put("status", 420)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.failed_unlock_token).put("status", 403)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token_answered)
			    .put("action", this.decisionTree)
			);
	}
}
