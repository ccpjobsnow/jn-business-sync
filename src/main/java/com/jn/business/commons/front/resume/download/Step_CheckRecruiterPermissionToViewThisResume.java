package com.jn.business.commons.front.resume.download;

import static com.jn.business.commons.front.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

class Step_CheckRecruiterPermissionToViewThisResume extends CcpNextStep{

	public Step_CheckRecruiterPermissionToViewThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		boolean exists = JnBusinessEntity.restriction_to_view_resume.exists(values);
		if(exists) {
			return  new CcpStepResult(values, 403, this);
		}
		
		return  new CcpStepResult(values, THIS_PROCESS_HAS_FINISHED, this);
	}

}
