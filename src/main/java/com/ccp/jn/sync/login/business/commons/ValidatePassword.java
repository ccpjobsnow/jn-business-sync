package com.ccp.jn.sync.login.business.commons;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class ValidatePassword extends CcpNextStep {

	final CcpPasswordHandler passwordHandler;
	final JnBusinessEntity entity;
	
	public ValidatePassword(CcpPasswordHandler passwordHandler, JnBusinessEntity entity) {
		this.passwordHandler = passwordHandler;
		this.entity = entity;
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpMapDecorator tables = values.getInternalMap("_tables");
		CcpMapDecorator pass = tables.getInternalMap(this.entity.name());
	
		String password = values.getAsString("password");
		String passwordDb = pass.getAsString("password");
		
		boolean incorrectPassword = this.passwordHandler.matches(passwordDb, password) == false;
		
		if(incorrectPassword) {
			return new CcpStepResult(values, 401, this);
		}
		
		
		return new CcpStepResult(values, 200, this);
	}

}
