package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.process.CcpStepResult;
import com.ccp.process.CcpNextStepFactory;

class VerifyResumeStatus  extends CcpNextStepFactory{

	public VerifyResumeStatus(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		Integer status = values.getAsIntegerNumber("status");
		boolean resumeIsNotActive = new Integer(1).equals(status) == false;
		if(resumeIsNotActive) {
			return new CcpStepResult(values, 404, this);
		}
		return  new CcpStepResult(values, 200, this);
	}

}
