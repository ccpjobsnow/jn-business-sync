package com.ccp.jn.sync.resumes.controller;

import java.util.Map;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.jn.sync.resumes.business.cache.GetRecruiterDomainsFromCache;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class GetRecruiterDomains {

	@CcpEspecification
	private CcpCache cache;

	
	public Map<String, Object> getRecruiterDomains(String prefix){
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		String cacheKey = JnCacheKeys.RECRUITERS_DOMAINS_KEY + JnConstants.DOT + prefix;
		GetRecruiterDomainsFromCache cacheLayer = new GetRecruiterDomainsFromCache(prefix);
		CcpMapDecorator cacheParameters = CcpConstants.emptyJson;
		
		CcpMapDecorator resumeData = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
		return resumeData.content;
	}
	
}
