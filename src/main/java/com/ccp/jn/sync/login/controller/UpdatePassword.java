package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class UpdatePassword {

	@CcpDependencyInject
	private CcpDao dao;

	private Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
		
		return new EvaluateToken()
				.addStep(401, new EvaluateTries(JnEntity.token_tries, 401, 403)
							.addStep(403, JnEntity.locked_token.getSaver(403))
						)
				.addStep(200, new ResetEntity("tries", 3, JnEntity.token_tries)
						.addStep(200, new TransferDataBetweenEntities(JnEntity.login_conflict, JnEntity.login_conflict_solved)
								.addStep(200, new TransferDataBetweenEntities(JnEntity.locked_password, JnEntity.unlocked_password)
										.addStep(200, new EvaluatePasswordStrength()
												.addStep(422, JnEntity.weak_password.getSaver(200))
												.addStep(200, new SaveLogin())
												)	
										)
								)
						)
				.goToTheNextStep(values).values;
		
	};

	
	public CcpMapDecorator execute (CcpMapDecorator values){
		
		/*
		 * Salvar senha desbloqueada
		 */
		CcpMapDecorator result = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).executeAction(this.decisionTree).andFinally()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
}
