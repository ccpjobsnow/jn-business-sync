package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.SavePassword;
import com.ccp.jn.sync.business.SaveWeakPasswordAction;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class SaveWeakPassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	public void execute (CcpMapDecorator parameters){

		CcpProcess saveWeakPassword = valores -> new SaveWeakPasswordAction()
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin()))
				.addStep(201, null)
				.goToTheNextStep(valores).values;
		
			this.crud
			.useThisId(parameters)
			.toBeginProcedure()
				.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()	
				.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
				.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenReturnStatus(404).andSo()
				.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenReturnStatus(201).andSo()
				.ifThisIdIsNotPresentInTable(JnBusinessEntity.weak_password).thenDoAnAction(saveWeakPassword).andFinally()
			.endThisProcedure()
			;

		
	}
}
