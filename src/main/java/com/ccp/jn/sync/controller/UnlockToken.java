package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.crud.ResetTable;
import com.ccp.especifications.db.crud.TransferDataBetweenTables;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.business.commons.password.ValidatePassword;
import com.ccp.jn.sync.business.commons.tries.EvaluateTries;
import com.ccp.jn.sync.business.login.LockLogin;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class UnlockToken {
	
	@CcpEspecification
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
				.goToTheNextStep(values).data;
		
	};

	@CcpEspecification
	private CcpDbCrud crud;
	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);

		this.crud.findById(values,  
				 new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.locked_token).put("status", 422)
			    ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.request_unlock_token).put("status", 420)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.failed_unlock_token_today).put("status", 403)
			    ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.request_unlock_token_answered)
			    .put("action", this.decisionTree)
			);
		return json;
	}
}
