package com.ccp.jn.sync.resumes.crud.controller;

import java.util.ArrayList;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnCacheKeys;
import com.jn.commons.JnConstants;

public class GetRecruiterDomains {

	@CcpDependencyInject
	private CcpCache cache;

	
	public CcpMapDecorator execute(String firstLetters){
		int cacheExpires = JnConstants.ONE_HOUR_IN_SECONDS;
		String cacheKey = JnCacheKeys.RECRUITERS_DOMAINS_KEY + JnConstants.DOT + firstLetters;
		CcpMapTransform<CcpMapDecorator> cacheLayer = valores -> JnBusinessEntity.recruiter_domains.get(new CcpMapDecorator().put("prefix", firstLetters), values -> values.put("domains", new ArrayList<>()));
		CcpMapDecorator cacheParameters = CcpConstants.EMPTY_JSON;
		
		CcpMapDecorator rhList = this.cache.get(cacheKey, cacheParameters, cacheLayer, cacheExpires);
		return rhList;
	}
	
}
