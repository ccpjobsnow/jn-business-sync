package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpNextStepFactory;
import com.ccp.process.CcpStepResult;

import static com.jn.business.resume.download.Steps.*;

class DownloadThisResume extends CcpNextStepFactory {
	private final  CcpFileBucket fileBucket;
	public DownloadThisResume(String businessName, CcpFileBucket fileBucket) {
		super(businessName);
		this.fileBucket = fileBucket;
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		String resume = this.getResume(values);
		
		CcpMapDecorator newValues = values.put("resume", resume);
		
		String viewMode = values.getAsString("viewMode");
		
		if("TEXT".equals(viewMode)) {
			return new CcpStepResult(newValues, IT_IS_A_TEXT_MODE_TO_VIEW_THIS_RESUME, this);

		}
		return  new CcpStepResult(newValues, 200, this);
	}

	private String getResume(CcpMapDecorator values) {
		String professional = values.getAsString("professional");
		String resumeHash = values.getAsString("resumeHash");
		
		String tenant = System.getenv("tenant");
	
		String resume = this.fileBucket.read(tenant, "curriculos", professional + "/" + resumeHash);
		return resume;
	}
}
