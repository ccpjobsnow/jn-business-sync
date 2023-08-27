package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;

public class ValidatePassword extends CcpNextStep {

	final CcpPasswordHandler passwordHandler = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
	final JnEntity entity;
	
	public ValidatePassword(JnEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpMapDecorator entities = values.getInternalMap("_entities");
		CcpMapDecorator pass = entities.getInternalMap(this.entity.name());
	
		String password = values.getAsString("password");
		String passwordDb = pass.getAsString("password");
		
		boolean incorrectPassword = this.passwordHandler.matches(passwordDb, password) == false;
		
		if(incorrectPassword) {
			return new CcpStepResult(values, 401, this);
		}
		
		
		return new CcpStepResult(values, 200, this);
	}

}
