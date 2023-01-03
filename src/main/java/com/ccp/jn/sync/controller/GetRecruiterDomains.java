package com.ccp.jn.backend.site.controller;

import java.util.Map;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.cache.CcpCache;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class GetRecruiterDomains {

	@CcpDependencyInject
	private CcpCache cache;

	
	public Map<String, Object> getRecruiterDomains(String prefix){
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		String cacheKey = JnCacheKeys.RECRUITERS_DOMAINS_KEY + JnConstants.DOT + prefix;
		GetRecruiterDomainsFromCache cacheLayer = new GetRecruiterDomainsFromCache(prefix);
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		
		CcpMapDecorator resumeData = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
		return resumeData.content;
	}
	
}
