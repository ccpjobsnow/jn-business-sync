package com.jn.business.commons.front.resume.download;

import static com.jn.business.commons.front.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_GET_THIS_RESUME_FROM_BUCKET_THEN;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnTopic;

public class Step_SaveViewResume extends CcpNextStep{

	public Step_SaveViewResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		JnTopic.SAVE_RESUME_VIEW.sendToTopic(values);
		return  new CcpStepResult(values, IF_GET_THIS_RESUME_FROM_BUCKET_THEN, this);
	}

}
