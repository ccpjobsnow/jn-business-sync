package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.process.CcpStepResult;
import com.ccp.process.CcpNextStepFactory;
import com.jn.commons.JnBusinessEntity;

class CheckRecruiterPermissionToViewThisResume extends CcpNextStepFactory{

	public CheckRecruiterPermissionToViewThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		String professional = values.getAsString("profesional");
		String recruiter = values.getAsString("recruiter");
		boolean exists = JnBusinessEntity.restriction_to_view_resume.exists(recruiter + "_" + professional);
		if(exists) {
			return  new CcpStepResult(values, 403, this);
		}
		
		return  new CcpStepResult(values, 200, this);
	}

}
