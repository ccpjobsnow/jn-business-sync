package com.ccp.jn.sync.common.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class DownloadResume implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);

	private CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);

	public CcpMapDecorator apply(CcpMapDecorator values) {

		String viewType = values.getAsString("viewType");
		String resume = values.getAsString("resume");

		
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		CcpMapTransform<String> cacheLayer = vals-> this.bucket.read(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + viewType, resume);
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + JnConstants.DOT + viewType;

		String resumeInBase64 = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
	
		CcpMapDecorator put = new CcpMapDecorator().put("resumeInBase64", resumeInBase64);

		
		return put;
	}

}
