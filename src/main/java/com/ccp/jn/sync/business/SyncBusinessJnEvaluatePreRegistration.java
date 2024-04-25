package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.CcpSuccessStatus;
import com.jn.commons.entities.JnEntityPreRegistration;

public class SyncBusinessJnEvaluatePreRegistration extends CcpNextStep{

	public SyncBusinessJnEvaluatePreRegistration(CcpNextStep nextProcess) {
		this.addMostExpectedStep(nextProcess);
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {
		
		if(JnEntityPreRegistration.INSTANCE.exists(values) == false) {
			return new CcpStepResult(values, JnProcessStatus.preRegistrationIsMissing.status, this);
		}
		
		return new CcpStepResult(values, new CcpSuccessStatus().status(), this);
	}

}
