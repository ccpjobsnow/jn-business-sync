package com.ccp.jn.sync.resumes.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class RemoveResume {
	
	@CcpEspecification
	private CcpFileBucket bucket;

	@CcpEspecification
	private CcpCache cache;

	
	public Map<String, Object> execute (String resume){
		
		this.removeFromBucket(resume, JnConstants.SLASH_FILE);
		this.removeFromBucket(resume, JnConstants.SLASH_TEXT);

		this.removeFromDatabase(JnBusinessEntity.candidate_resume, resume);
		this.removeFromDatabase(JnBusinessEntity.candidate, resume);
		
		this.removeFromCache(resume, JnConstants.SLASH_FILE);
		this.removeFromCache(resume, JnConstants.SLASH_TEXT);
	
		return new CcpMapDecorator().content;
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
