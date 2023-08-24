package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpInstanceInjection;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;

public class SavePassword extends CcpNextStep{

	private CcpPasswordHandler passwordHandler = CcpInstanceInjection.getInstance(CcpPasswordHandler.class);
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		String password = values.getAsString("password");
		String passwordHash = this.passwordHandler.getPasswordHash(password);
		JnEntity.password.createOrUpdate(values.put("password", passwordHash));
		return new CcpStepResult(values, 200, this);
	}

}
