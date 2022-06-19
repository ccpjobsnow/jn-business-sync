package com.jn.business.commons.resume.download;

import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

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
		boolean isNotTheSamePerson = recruiter.equals(professional) == false;
		if(isNotTheSamePerson) {
			return new CcpStepResult(values, IF_IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, this);
		}
		return  new CcpStepResult(values, IF_THE_OWNER_IS_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, this);
	}

}
