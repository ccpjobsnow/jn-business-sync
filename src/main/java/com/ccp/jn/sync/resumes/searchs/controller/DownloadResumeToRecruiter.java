package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;


public class DownloadResumeToRecruiter {
	
	@CcpDependencyInject
	private CcpFileBucket bucket;
	
	@CcpDependencyInject
	private CcpCache cache;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter);
		
		this.crud.findById(values,  
				new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.candidate_resume).put("status", 404)
				,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.denied_view_to_recruiter).put("status", 403)
			);

		JnBusinessEntity.recruiter_view_resume.save(values);
		
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		CcpMapTransform<String> cacheLayer = vals-> this.bucket.read(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + viewType, resume);
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + JnConstants.DOT + viewType;

		String resumeInBase64 = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
	
		return new CcpMapDecorator().put("resumeInBase64", resumeInBase64).content;
	}
}
