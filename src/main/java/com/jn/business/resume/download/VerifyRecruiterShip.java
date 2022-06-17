package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.exceptions.process.CcpStepResult;
import com.ccp.process.CcpNextStepFactory;
import com.jn.commons.JnBusinessEntity;
import static com.jn.business.resume.download.Steps.*;

class VerifyRecruiterShip  extends CcpNextStepFactory{

	public VerifyRecruiterShip(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		String recruiter = values.getAsString("recruiter");
		try {
			JnBusinessEntity.recruiter.get(recruiter);
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 401, this);
		}
		
		boolean isFreelancer = new CcpStringDecorator(recruiter).email().isNonProfessional();
		
		if(isFreelancer) {
			return  new CcpStepResult(values, 200, this);
		}
		
		
		return new CcpStepResult(values, IS_A_CONSULTING_RECRUITER, this);
	}

}
