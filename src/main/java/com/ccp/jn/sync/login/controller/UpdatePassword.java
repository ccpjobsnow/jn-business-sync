package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;

public class UpdatePassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	private CcpProcess decisionTree = values ->{
		
		return new EvaluateToken()
				.addStep(401, new EvaluateTries(JnBusinessEntity.token_tries, 401, 403)
							.addStep(403, JnBusinessEntity.locked_token.getSaver(403))
						)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnBusinessEntity.token_tries)
						.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.login_conflict, JnBusinessEntity.login_conflict_solved)
								.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.locked_password, JnBusinessEntity.unlocked_password)
										.addStep(200, new EvaluatePasswordStrength()
												.addStep(422, JnBusinessEntity.weak_password.getSaver(200))
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
			.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()
			.loadThisIdFromTable(JnBusinessEntity.token_tries).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).returnStatus(404).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).executeAction(this.decisionTree).andFinally()	
		.endThisProcedure()
		;
	}
}
