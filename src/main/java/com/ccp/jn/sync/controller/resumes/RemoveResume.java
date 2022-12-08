package com.ccp.jn.sync.controller.resumes;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class RemoveResume {
	
	@CcpSpecification
	private CcpFileBucket bucket;

	@CcpSpecification
	private CcpCache cache;

	
	public Map<String, Object> execute (String resume){
		
		this.removeFromBucket(resume, "file");
		this.removeFromBucket(resume, "text");

		this.removeFromDatabase(JnBusinessEntity.candidate_resume, resume);
		this.removeFromDatabase(JnBusinessEntity.candidate, resume);
		
		this.removeFromCache(resume, "file");
		this.removeFromCache(resume, "text");
		
		this.saveResumeExclusion(resume);
	
		return new CcpMapDecorator().content;
	}


	private void saveResumeExclusion(String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		JnBusinessEntity.resume_exclusion.save(values);
	}


	private void removeFromDatabase(JnBusinessEntity table, String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		
		table.remove(values);
	}


	private void removeFromBucket(String resume, String type) {
		this.bucket.remove(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + type, resume);
	}


	private void removeFromCache(String resume, String type) {
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + type.replace("/", ".");
		
		this.cache.remove(cacheKey);
	}
}
