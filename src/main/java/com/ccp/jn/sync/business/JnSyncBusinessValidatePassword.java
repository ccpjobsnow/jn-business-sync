package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

public class JnSyncBusinessValidatePassword extends CcpNextStep {

	final CcpPasswordHandler passwordHandler = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
	final CcpEntity entity;
	
	public JnSyncBusinessValidatePassword(CcpEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpMapDecorator entities = values.getInternalMap("_entities");
		CcpMapDecorator pass = entities.getInternalMap(this.entity.name());
	
		String password = values.getAsString("password");
		String passwordDb = pass.getAsString("password");
		
		boolean incorrectPassword = this.passwordHandler.matches(password, passwordDb) == false;
		
		if(incorrectPassword) {
			return new CcpStepResult(values, JnProcessStatus.wrongPassword.status, this);
		}
		
		
		return new CcpStepResult(values, 200, this);
	}

}
