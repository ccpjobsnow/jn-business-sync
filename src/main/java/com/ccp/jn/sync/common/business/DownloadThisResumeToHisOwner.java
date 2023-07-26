package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class DownloadThisResumeToHisOwner implements CcpProcess {

	private final DownloadResume downloadResume = CcpDependencyInjection.getInjected(DownloadResume.class);
	

	private DownloadThisResumeToHisOwner(CcpFileBucket bucket, CcpCache cache) {
	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		CcpMapDecorator candidate = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.candidate.name());
		
		String viewType = values.getAsString("viewType");
		String resume = candidate.getAsString("resume");

		JnBusinessEntity.candidate_view_resume.save(new CcpMapDecorator().put("resume", resume).put("viewType", viewType));
		
		CcpMapDecorator execute = this.downloadResume.execute(values);
		
		return execute;
	}

	
	
	
}
