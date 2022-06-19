package com.jn.business.commons.resume.download;

import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

class Step_CheckExistenceOfThisRecruiter  extends CcpNextStep{

	public Step_CheckExistenceOfThisRecruiter(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		String recruiter = values.getAsString("recruiter");
		try {
			JnBusinessEntity.recruiter.get(recruiter);
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 401, this);
		}
		
		boolean isFreelancer = new CcpStringDecorator(recruiter).email().isFreelancerDomain();
		
		if(isFreelancer) {
			return  new CcpStepResult(values, IF_IS_A_FREELANCER_RECRUITER_THEN, this);
		}
		
		
		return new CcpStepResult(values, IF_IS_A_CONSULTING_RECRUITER_THEN, this);
	}

}
