package com.ccp.jn.sync.business.login;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class ValidateLogin extends CcpNextStep {

	final CcpPasswordHandler passwordHandler;
	
	public ValidateLogin(CcpPasswordHandler passwordHandler) {
		this.passwordHandler = passwordHandler;
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator pass = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.password.name());
	
		String password = values.getAsString("password");
		String passwordDb = pass.getAsString("password");
		
		boolean senhaIncorreta = this.passwordHandler.matches(passwordDb, password) == false;
		
		if(senhaIncorreta) {
			return new CcpStepResult(values, 401, this);
		}
		return new CcpStepResult(values, 200, this);
	}

}
