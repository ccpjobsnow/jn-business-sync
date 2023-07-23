package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class DownloadThisResumeToHisOwner implements CcpProcess {

	private final CcpFileBucket bucket;
	private final CcpCache cache;
	

	public DownloadThisResumeToHisOwner(CcpFileBucket bucket, CcpCache cache) {
		this.bucket = bucket;
		this.cache = cache;
	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		CcpMapDecorator candidate = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.candidate.name());
		
		String viewType = values.getAsString("viewType");
		String resume = candidate.getAsString("resume");

		JnBusinessEntity.candidate_view_resume.save(new CcpMapDecorator().put("resume", resume).put("viewType", viewType));
		
		DownloadResume downloadResume = new DownloadResume(this.bucket, this.cache);
		
		CcpMapDecorator execute = downloadResume.execute(values);
		
		return execute;
	}

	
	
	
}
