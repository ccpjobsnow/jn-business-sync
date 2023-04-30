package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SaveLoginRequest {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		CcpProcess action = valores -> this.mensageriaSender.send(valores, JnBusinessTopic.sendUserToken);

		this.crud
		.useThisId(values)
		.toBeginProcedure()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_token).thenReturnStatus(403).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.locked_password).thenReturnStatus(401).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).thenDoAnAction(action).andSo()
			.ifThisIdIsPresentInTable(JnBusinessEntity.login).thenReturnStatus(409).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.pre_registration).thenReturnStatus(201).andSo()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.password).thenReturnStatus(202).andFinally()
		.endThisProcedure()
		;

	}
}
