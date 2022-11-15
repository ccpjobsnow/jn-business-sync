package com.ccp.jn.sync.resumes.business.cache;

import java.util.ArrayList;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnBusinessEntity;

public class GetRecruiterDomainsFromCache implements CcpMapTransform<CcpMapDecorator>{
	
	private final String prefix;
	
	public GetRecruiterDomainsFromCache(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public CcpMapDecorator transform(CcpMapDecorator valores) {
		CcpMapDecorator response = JnBusinessEntity.recruiter_domains.get(new CcpMapDecorator().put("prefix", prefix), values -> values.put("domains", new ArrayList<>()));

		return response;
	}






}
