package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class UnlockToken {
	
	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnEntity.request_unlock_token_answered)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnEntity.unlock_token_tries)
						.addStep(200, new TransferDataBetweenTables(JnEntity.locked_token, JnEntity.unlocked_token)
							)
						)
				.addStep(401, new EvaluateTries(JnEntity.unlock_token_tries, 401, 429)
						.addStep(429, JnEntity.locked_password.getSaver(429))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDao crud;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInTable(JnEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsNotPresentInTable(JnEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInTable(JnEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnEntity.request_unlock_token_answered).executeAction(this.decisionTree).andFinally()
		.endThisProcedure()
		;

		
	}
}
