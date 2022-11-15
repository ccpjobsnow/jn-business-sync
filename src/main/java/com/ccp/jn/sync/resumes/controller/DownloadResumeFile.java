package com.ccp.jn.sync.resumes.controller;

import java.util.Map;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.sync.resumes.business.cache.ReadResumeFromBucket;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;


public class DownloadResumeFile {
	
	@CcpEspecification
	private CcpFileBucket bucket;
	
	@CcpEspecification
	private CcpCache cache;
	
	public Map<String, Object> execute (String resume){
	
		String type = JnConstants.SLASH_FILE;
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		CcpMapDecorator cacheParameters = CcpConstants.emptyJson;
		ReadResumeFromBucket cacheLayer = new ReadResumeFromBucket(this.bucket, resume, type);
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + type.replace("/", ".");

		String resumeInBase64 = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
	
		return new CcpMapDecorator().put("resumeInBase64", resumeInBase64).content;
	}
}
