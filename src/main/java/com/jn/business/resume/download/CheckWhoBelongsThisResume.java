package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.process.CcpStepResult;
import com.ccp.process.CcpNextStepFactory;
import static com.jn.business.resume.download.Steps.*;

class CheckWhoBelongsThisResume  extends CcpNextStepFactory{

	public CheckWhoBelongsThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		String professional = values.getAsString("profesional");
		String recruiter = values.getAsString("recruiter");
		boolean isNotTheSamePerson = recruiter.equals(professional) == false;
		if(isNotTheSamePerson) {
			return new CcpStepResult(values, IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME, this);
		}
		return  new CcpStepResult(values, 200, this);
	}

}
