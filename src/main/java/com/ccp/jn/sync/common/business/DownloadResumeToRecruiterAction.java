package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;

public class DownloadResumeToRecruiterAction implements CcpProcess {

	private final DownloadResume downloadResume = CcpDependencyInjection.getInjected(DownloadResume.class);
			
	private DownloadResumeToRecruiterAction(CcpFileBucket bucket, CcpCache cache) {
	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		String recruiter = values.getAsString("recruiter");
		String viewType = values.getAsString("viewType");
		String resume = values.getAsString("resume");
		
		JnEntity.recruiter_view_resume.createOrUpdate(new CcpMapDecorator().put("resume", resume)
				.put("viewType", viewType)
				.put("recruiter", recruiter));
		
		
		CcpMapDecorator execute = this.downloadResume.execute(values);
		
		return execute;
	}

	
	
	
}
