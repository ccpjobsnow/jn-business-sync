package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDao crud;

	public void execute (CcpMapDecorator parameters){

		CcpProcess saveWeakPassword = valores -> JnEntity.weak_password.getSaver(200)
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin()))
				.addStep(201, null)
				.goToTheNextStep(valores).values;
		
			this.crud
			.useThisId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromTable(JnEntity.user_stats).andSo()	
				.ifThisIdIsPresentInTable(JnEntity.locked_token).returnStatus(403).and()
				.ifThisIdIsNotPresentInTable(JnEntity.login_token).returnStatus(404).and()
				.ifThisIdIsNotPresentInTable(JnEntity.pre_registration).returnStatus(201).and()
				.ifThisIdIsNotPresentInTable(JnEntity.weak_password).executeAction(saveWeakPassword).andFinally()
			.endThisProcedure()
			;

		
	}
}
