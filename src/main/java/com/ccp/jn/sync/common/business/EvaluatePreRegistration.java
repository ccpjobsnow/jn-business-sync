package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.SuccessStatus;
import com.jn.commons.JnEntity;

public class EvaluatePreRegistration extends CcpNextStep{

	public EvaluatePreRegistration(CcpNextStep nextProcess) {
		this.addMostExpectedStep(nextProcess);
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		if(JnEntity.pre_registration.exists(values) == false) {
			return new CcpStepResult(values, JnProcessStatus.preRegistrationIsMissing.status, this);
		}
		
		return new CcpStepResult(values, new SuccessStatus().status(), this);
	}

}
