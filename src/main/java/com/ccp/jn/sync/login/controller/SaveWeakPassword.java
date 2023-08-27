package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.jn.sync.common.business.SaveLogin;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.JnEntity;

public class SaveWeakPassword {

	private final SavePassword passwordHandler = new SavePassword();

	public CcpMapDecorator execute (CcpMapDecorator parameters){

		//TODO ESSE CARA VAI PRECISAR RETORNAR O 201???
		Function<CcpMapDecorator, CcpMapDecorator> saveWeakPassword = valores -> this.passwordHandler
				.addStep(CcpProcessStatus.nextStep, new SaveLogin())
				.goToTheNextStep(valores).values;
		
		 CcpMapDecorator values = new CalculateId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats).andSo()	
				.ifThisIdIsNotPresentInEntity(JnEntity.password).executeAction(saveWeakPassword).andFinally()
			.endThisProcedureRetrievingTheResultingData();
		 return values;
		
	}
}
