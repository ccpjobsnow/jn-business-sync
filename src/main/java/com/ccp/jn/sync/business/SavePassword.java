package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class SavePassword extends CcpNextStep{

	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		String password = values.getAsString("password");
		String passwordHash = this.passwordHandler.getPasswordHash(password);
		JnBusinessEntity.password.save(values.put("password", passwordHash));
		return new CcpStepResult(values, 200, this);
	}

}