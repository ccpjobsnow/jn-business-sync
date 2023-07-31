package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {

	@CcpDependencyInject
	private CcpDao dao;

	public void execute (CcpMapDecorator parameters){

		CcpProcess saveWeakPassword = valores -> JnEntity.weak_password.getSaver(200)
				.addStep(200, new SavePassword()
						.addStep(200, new SaveLogin()))
				.addStep(201, null)
				.goToTheNextStep(valores).values;
		
			this.dao
			.useThisId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats).andSo()	
				.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(404).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.weak_password).executeAction(saveWeakPassword).andFinally()
			.endThisProcedure()
			;

		
	}
}
