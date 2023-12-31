package com.ccp.jn.sync.business;
 
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

public class JnSyncBusinessValidatePassword extends CcpNextStep {

	final CcpPasswordHandler passwordHandler = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
	final JnProcessStatus wrongPasswordStatus;
	final CcpEntity entity;
	final String fieldName;
	
	public JnSyncBusinessValidatePassword(CcpEntity entity, JnProcessStatus wrongPasswordStatus, String fieldName) {
		this.wrongPasswordStatus = wrongPasswordStatus;
		this.fieldName = fieldName;
		this.entity = entity;
	}
	
	public JnSyncBusinessValidatePassword(CcpEntity entity) {
		this(entity, JnProcessStatus.wrongPassword, "password");
	}
	
	
	@Override
	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {
		
		CcpJsonRepresentation entities = values.getInnerJson("_entities");
		CcpJsonRepresentation pass = entities.getInnerJson(this.entity.name());
	
		String password = values.getAsString("password");
		String passwordDb = pass.getAsString(this.fieldName);
		
		boolean incorrectPassword = this.passwordHandler.matches(password, passwordDb) == false;
		
		if(incorrectPassword) {
			return new CcpStepResult(values, this.wrongPasswordStatus.status, this);
		}
		
		
		return new CcpStepResult(values, 200, this);
	}

}
