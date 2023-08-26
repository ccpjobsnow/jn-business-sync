package com.ccp.jn.sync.resumes.crud.controller;

import java.util.ArrayList;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpInstanceInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;
import com.jn.commons.JnEntity;

public class GetRecruiterDomains {

	private CcpCache cache = CcpInstanceInjection.getInstance(CcpCache.class);

	
	public CcpMapDecorator apply(String firstLetters){
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		String cacheKey = JnCacheKeys.RECRUITERS_DOMAINS_KEY + JnConstants.DOT + firstLetters;
		CcpMapTransform<CcpMapDecorator> cacheLayer = valores -> JnEntity.recruiter_domains.getOneById(new CcpMapDecorator().put("prefix", firstLetters), values -> values.put("domains", new ArrayList<>()));
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		
		CcpMapDecorator rhList = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
		return rhList;
	}
	
}
