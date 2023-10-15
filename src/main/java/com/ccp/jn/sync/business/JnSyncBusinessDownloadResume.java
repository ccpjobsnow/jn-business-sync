package com.ccp.jn.sync.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.utils.JnCacheKeys;
import com.jn.commons.utils.JnConstants;

public class JnSyncBusinessDownloadResume implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{


	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		
		CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);

		String viewType = values.getAsString("viewType");
		String resume = values.getAsString("resume");

		
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		CcpMapTransform<String> cacheLayer = vals-> bucket.read(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + viewType, resume);
		String cacheKey = JnCacheKeys.RESUMES_KEY + JnConstants.DOT + resume + JnConstants.DOT + viewType;

		String resumeInBase64 = cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
	
		CcpMapDecorator put = new CcpMapDecorator().put("resumeInBase64", resumeInBase64);

		
		return put;
	}

}