package com.jn.business.commons.front.resume.download;

import static com.jn.business.commons.front.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

class Step_CheckWhoBelongsThisResume  extends CcpNextStep{

	public Step_CheckWhoBelongsThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		String professional = values.getAsString("profesional");
		String recruiter = values.getAsString("recruiter");
		
		boolean isTheSamePerson = recruiter.equals(professional);
		
		if(isTheSamePerson) {
			return  new CcpStepResult(values, IF_THE_OWNER_IS_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, this);
		}

		int status = values.getAsIntegerNumber("status");
		
		boolean resumeIsNotActive = new Integer(1).equals(status) == false;
		
		if(resumeIsNotActive) {
			return new CcpStepResult(values, 406, this);
		}
		
		return new CcpStepResult(values, IF_IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, this);
	}

}
