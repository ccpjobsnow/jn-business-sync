package com.jn.business.commons.resume.download;

import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_THIS_RESUME_HAS_A_VALID_ALIAS_THEN;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

class Step_CheckActiveResume  extends CcpNextStep{

	public Step_CheckActiveResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		Integer status = values.getAsIntegerNumber("status");
		boolean resumeIsNotActive = new Integer(1).equals(status) == false;
		if(resumeIsNotActive) {
			return new CcpStepResult(values, 404, this);
		}
		return  new CcpStepResult(values, IF_THIS_RESUME_HAS_A_VALID_ALIAS_THEN, this);
	}

}
