package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.business.EvaluateToken;
import com.ccp.jn.sync.business.LockToken;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.SaveWeakPasswordAction;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.ResetTable;

public class UpdatePassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
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
												.addStep(422, new SaveWeakPasswordAction())
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
		.toBeginProcedure()
			.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()
			.loadThisIdFromTable(JnBusinessEntity.token_tries).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).thenReturnStatus(409).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenReturnStatus(201).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).thenDoAnAction(this.decisionTree).andFinally()	
		.endThisProcedure()
		;
	}
}
