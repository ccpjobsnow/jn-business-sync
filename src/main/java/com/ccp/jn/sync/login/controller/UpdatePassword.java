package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;

public class UpdatePassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDao crud;

	
	private CcpProcess decisionTree = values ->{
		
		return new EvaluateToken()
				.addStep(401, new EvaluateTries(JnEntity.token_tries, 401, 403)
							.addStep(403, JnEntity.locked_token.getSaver(403))
						)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnEntity.token_tries)
						.addStep(200, new TransferDataBetweenTables(JnEntity.login_conflict, JnEntity.login_conflict_solved)
								.addStep(200, new TransferDataBetweenTables(JnEntity.locked_password, JnEntity.unlocked_password)
										.addStep(200, new EvaluatePasswordStrength()
												.addStep(422, JnEntity.weak_password.getSaver(200))
												.addStep(200, new SaveLogin())
												)	
										)
								)
						)
				.goToTheNextStep(values).values;
		
	};

	
	public void execute (CcpMapDecorator values){
		
		/*
		 * Salvar senha desbloqueada
		 */
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromTable(JnEntity.user_stats).andSo()
			.loadThisIdFromTable(JnEntity.token_tries).andSo()
			.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnEntity.password).executeAction(this.decisionTree).andFinally()	
		.endThisProcedure()
		;
	}
}
