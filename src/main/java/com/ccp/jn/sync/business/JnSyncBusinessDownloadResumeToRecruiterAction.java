package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntity;

public class JnSyncBusinessDownloadResumeToRecruiterAction implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

			
	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		JnSyncBusinessDownloadResume downloadResume = new JnSyncBusinessDownloadResume();
		
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
