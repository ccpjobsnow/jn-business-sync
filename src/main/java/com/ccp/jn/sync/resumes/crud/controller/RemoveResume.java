package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;
import com.jn.commons.JnEntity;

public class RemoveResume {
	
	private CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);

	private CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);

	public void execute (String resume){
		
		this.removeFromBucket(resume, "file");
		this.removeFromBucket(resume, "text");

		this.removeFromDatabase(JnEntity.candidate_resume, resume);
		this.removeFromDatabase(JnEntity.candidate, resume);
		
		this.removeFromCache(resume, "file");
		this.removeFromCache(resume, "text");
		
		this.saveResumeExclusion(resume);
	
	}


	private void saveResumeExclusion(String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		JnEntity.resume_exclusion.createOrUpdate(values);
	}


	private void removeFromDatabase(JnEntity entity, String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		
		entity.delete(values);
	}


	private void removeFromBucket(String resume, String type) {
		this.bucket.remove(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + type, resume);
	}


	private void removeFromCache(String resume, String type) {
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + type.replace("/", ".");
		
		this.cache.remove(cacheKey);
	}
}
