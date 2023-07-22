package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.business.SaveLogin;
import com.ccp.jn.sync.business.SavePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class SaveWeakPassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	public void execute (CcpMapDecorator parameters){

		CcpProcess saveWeakPassword = valores -> JnBusinessEntity.weak_password.getSaver(200)
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin()))
				.addStep(201, null)
				.goToTheNextStep(valores).values;
		
			this.crud
			.useThisId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromTable(JnBusinessEntity.user_stats).andSo()	
				.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).returnStatus(403).and()
				.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.login_token).returnStatus(404).and()
				.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.pre_registration).returnStatus(201).and()
				.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.weak_password).executeAction(saveWeakPassword).andFinally()
			.endThisProcedure()
			;

		
	}
}
