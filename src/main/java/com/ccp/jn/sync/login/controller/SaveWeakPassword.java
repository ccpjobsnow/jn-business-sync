package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.UseThisId;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {

	private final SavePassword passwordHandler = new SavePassword();

	public void execute (CcpMapDecorator parameters){

		//TODO ESSE CARA VAI PRECISAR RETORNAR O 201???
		Function<CcpMapDecorator, CcpMapDecorator> saveWeakPassword = valores -> JnEntity.weak_password.getSaver(CcpProcessStatus.nextStep)
				.addStep(CcpProcessStatus.nextStep, this.passwordHandler.addStep(CcpProcessStatus.nextStep, new SaveLogin()))
				.goToTheNextStep(valores).values;
		
		 new UseThisId(parameters, new CcpMapDecorator())
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats).andSo()	
				.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
				.ifThisIdIsNotPresentInEntity(JnEntity.weak_password).executeAction(saveWeakPassword).andFinally()
			.endThisProcedure()
			;

		
	}
}
