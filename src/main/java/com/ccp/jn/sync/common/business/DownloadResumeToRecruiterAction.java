package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;

public class DownloadResumeToRecruiterAction implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

			
	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		DownloadResume downloadResume = new DownloadResume();
		
		String recruiter = values.getAsString("recruiter");
		String viewType = values.getAsString("viewType");
		String resume = values.getAsString("resume");
		
		JnEntity.recruiter_view_resume.createOrUpdate(new CcpMapDecorator().put("resume", resume)
				.put("viewType", viewType)
				.put("recruiter", recruiter));
		
		
		CcpMapDecorator execute = downloadResume.apply(values);
		
		return execute;
	}

	
	
	
}
