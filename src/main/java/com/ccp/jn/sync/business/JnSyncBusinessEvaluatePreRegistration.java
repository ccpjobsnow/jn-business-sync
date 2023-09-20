package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.CcpSuccessStatus;
import com.jn.commons.entities.JnEntityPreRegistration;

public class JnSyncBusinessEvaluatePreRegistration extends CcpNextStep{

	public JnSyncBusinessEvaluatePreRegistration(CcpNextStep nextProcess) {
		this.addMostExpectedStep(nextProcess);
	}
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		if(new JnEntityPreRegistration().exists(values) == false) {
			return new CcpStepResult(values, JnProcessStatus.preRegistrationIsMissing.status, this);
		}
		
		return new CcpStepResult(values, new CcpSuccessStatus().status(), this);
	}

}
