package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.entities.JnEntityPassword;

public class JnSyncBusinessSavePassword extends CcpNextStep{

	private CcpPasswordHandler passwordHandler = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		String password = values.getAsString("password");
		String passwordHash = this.passwordHandler.getPasswordHash(password);
		new JnEntityPassword().createOrUpdate(values.put("password", passwordHash));
		return new CcpStepResult(values, 200, this);
	}

}
