package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.jn.commons.entities.JnEntityCandidate;
import com.jn.commons.entities.JnEntityCandidateResume;
import com.jn.commons.entities.JnEntityResumeExclusion;
import com.jn.commons.utils.JnCacheKeys;
import com.jn.commons.utils.JnConstants;

public class RemoveResume {
	


	public void execute (String resume){
		this.removeFromBucket(resume, "file");
		this.removeFromBucket(resume, "text");

		this.removeFromDatabase(new JnEntityCandidateResume(), resume);
		this.removeFromDatabase(new JnEntityCandidate(), resume);
		
		this.removeFromCache(resume, "file");
		this.removeFromCache(resume, "text");
		
		this.saveResumeExclusion(resume);
	
	}


	private void saveResumeExclusion(String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		new JnEntityResumeExclusion().createOrUpdate(values);
	}


	private void removeFromDatabase(CcpEntity entity, String resume) {
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume);
		
		entity.delete(values);
	}


	private void removeFromBucket(String resume, String type) {
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		
		bucket.remove(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + type, resume);
	}


	private void removeFromCache(String resume, String type) {
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + type.replace("/", ".");
		
		CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);

		cache.remove(cacheKey);
	}
}
