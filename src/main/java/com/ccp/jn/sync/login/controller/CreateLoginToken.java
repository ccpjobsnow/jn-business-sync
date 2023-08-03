package com.ccp.jn.sync.login.controller;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class CreateLoginToken {

	@CcpDependencyInject
	private CcpDao dao;

	
	public CcpMapDecorator execute (String email, String language){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("language", language);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnTopic.sendUserToken.send(valores);

		CcpMapDecorator result = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(403).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(401).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).executeAction(action).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(409).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(201).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(202).andFinally()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
}
