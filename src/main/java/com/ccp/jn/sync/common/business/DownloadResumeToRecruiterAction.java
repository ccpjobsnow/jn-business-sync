package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class DownloadResumeToRecruiterAction implements CcpProcess {

	private final CcpFileBucket bucket;
	private final CcpCache cache;
	
	public DownloadResumeToRecruiterAction(CcpFileBucket bucket, CcpCache cache) {
		this.bucket = bucket;
		this.cache = cache;
	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		String recruiter = values.getAsString("recruiter");
		String viewType = values.getAsString("viewType");
		String resume = values.getAsString("resume");
		
		JnBusinessEntity.recruiter_view_resume.save(new CcpMapDecorator().put("resume", resume)
				.put("viewType", viewType)
				.put("recruiter", recruiter));
		
		DownloadResume downloadResume = new DownloadResume(this.bucket, this.cache);
		
		CcpMapDecorator execute = downloadResume.execute(values);
		
		return execute;
	}

	
	
	
}
