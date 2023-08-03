package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class UnlockToken {
	
	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnEntity.request_unlock_token_answered)
				.addStep(200, new ResetEntity("tries", 3, JnEntity.unlock_token_tries)
						.addStep(200, new TransferDataBetweenEntities(JnEntity.locked_token, JnEntity.unlocked_token)
							)
						)
				.addStep(401, new EvaluateTries(JnEntity.unlock_token_tries, 401, 429)
						.addStep(429, JnEntity.locked_password.getSaver(429))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDao dao;
	
	public CcpMapDecorator execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));
		CcpMapDecorator result = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInEntity(JnEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).executeAction(this.decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
}
