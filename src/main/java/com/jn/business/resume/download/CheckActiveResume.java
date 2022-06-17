package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStepFactory;
import com.ccp.process.CcpStepResult;

class CheckActiveResume  extends CcpNextStepFactory{

	public CheckActiveResume(String businessName) {
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
