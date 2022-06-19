package com.jn.business.commons.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

class Step_CheckRecruiterPermissionToViewThisResume extends CcpNextStep{

	public Step_CheckRecruiterPermissionToViewThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		String professional = values.getAsString("profesional");
		String recruiter = values.getAsString("recruiter");
		boolean exists = JnBusinessEntity.restriction_to_view_resume.exists(recruiter + "_" + professional);
		if(exists) {
			return  new CcpStepResult(values, 403, this);
		}
		
		return  new CcpStepResult(values, THIS_PROCESS_HAS_FINISHED, this);
	}

}
