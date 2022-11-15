package com.ccp.jn.sync.login.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.table.ResetTable;
import com.ccp.especifications.db.table.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.login.business.SaveLogin;
import com.ccp.jn.sync.login.business.commons.EvaluateTries;
import com.ccp.jn.sync.login.business.password.EvaluatePasswordStrength;
import com.ccp.jn.sync.login.business.password.EvaluateToken;
import com.ccp.jn.sync.login.business.password.LockToken;
import com.ccp.jn.sync.login.business.password.SaveWeakPassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class UpdatePassword {

	@CcpSpecification
	private CcpMensageriaSender mensageriaSender;
	
	@CcpSpecification
	private CcpDbCrud crud;

	
	private CcpProcess decisionTree = values ->{
		
		return new EvaluateToken()
				.addStep(401, new EvaluateTries(JnBusinessEntity.token_tries, 401, 403)
							.addStep(403, new LockToken())
						)
				.addStep(200, new ResetTable(JnBusinessEntity.token_tries)
						.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.login_conflict, JnBusinessEntity.login_conflict_solved)
								.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.locked_password, JnBusinessEntity.unlocked_password)
										.addStep(200, new EvaluatePasswordStrength()
												.addStep(422, new SaveWeakPassword())
												.addStep(200, new SaveLogin())
												)	
										)
								)
						)
				.goToTheNextStep(values).data;
		
	};

	
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
		/*
		 * Salvar senha desbloqueada
		 */
		this.crud.findById(values,  
				    new CcpMapDecorator().put("table", JnBusinessEntity.user_stats)
				   ,new CcpMapDecorator().put("table", JnBusinessEntity.token_tries)
    			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.password).put("action", this.decisionTree)
				);
		
		
		return values.content;
	}
}
