package com.ccp.jn.sync.resumes.crud.controller;

import java.util.ArrayList;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnCacheKeys;
import com.jn.commons.utils.JnConstants;

public class GetRecruiterDomains {


	
	public CcpMapDecorator apply(String firstLetters){
		CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		String cacheKey = JnCacheKeys.RECRUITERS_DOMAINS_KEY + JnConstants.DOT + firstLetters;
		CcpMapTransform<CcpMapDecorator> cacheLayer = valores -> JnEntity.recruiter_domains.getOneById(new CcpMapDecorator().put("prefix", firstLetters), values -> values.put("domains", new ArrayList<>()));
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		
		CcpMapDecorator rhList = cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
		return rhList;
	}
	
}
