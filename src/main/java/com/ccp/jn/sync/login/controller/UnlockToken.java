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
		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(422).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.request_unlock_token).thenReturnStatus(420).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.failed_unlock_token).thenReturnStatus(403).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token_answered).thenDoAnAction(this.decisionTree).andFinally()
		.endThisProcedure()
		;

		
	}
}
