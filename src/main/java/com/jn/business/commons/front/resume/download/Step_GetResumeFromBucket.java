package com.jn.business.commons.front.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

class Step_GetResumeFromBucket extends CcpNextStep {
	private final  CcpFileBucket fileBucket;

	

	public Step_GetResumeFromBucket(String businessName, CcpFileBucket fileBucket) {
		super(businessName);
		this.fileBucket = fileBucket;
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		String resume = this.getResume(values);
		
		CcpMapDecorator newValues = values.put("resume", resume);
		
		return  new CcpStepResult(newValues, 200, this);
	}

	private String getResume(CcpMapDecorator values) {
		String professional = values.getAsString("professional");
		String resumeHash = values.getAsString("resumeHash");
		
		String tenant = System.getenv("tenant");
		String viewMode = values.getAsString("viewMode");
		
		String pathToResume = professional + "/" + viewMode + "_" + resumeHash;

		String resume = this.fileBucket.read(tenant, "resumes", pathToResume);
		return resume;
	}
}
