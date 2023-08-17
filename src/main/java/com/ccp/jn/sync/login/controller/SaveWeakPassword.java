package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {


	@CcpDependencyInject
	private CcpDao dao;

	public void execute (CcpMapDecorator parameters){
		//TODO ESSE CARA VAI PRECISAR RETORNAR O 201???
		Function<CcpMapDecorator, CcpMapDecorator> saveWeakPassword = valores -> JnEntity.weak_password.getSaver(CcpProcessStatus.nextStep)
				.addStep(CcpProcessStatus.nextStep, new SavePassword()
						.addStep(CcpProcessStatus.nextStep, new SaveLogin()))
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
