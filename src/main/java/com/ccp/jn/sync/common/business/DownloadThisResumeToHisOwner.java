package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;

public class DownloadThisResumeToHisOwner implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	private final DownloadResume downloadResume = new DownloadResume();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {

		CcpMapDecorator candidate = values.getInternalMap("_entities").getInternalMap(JnEntity.candidate.name());
		
		String viewType = values.getAsString("viewType");
		String resume = candidate.getAsString("resume");

		JnEntity.candidate_view_resume.createOrUpdate(new CcpMapDecorator().put("resume", resume).put("viewType", viewType));
		
		CcpMapDecorator execute = this.downloadResume.apply(values);
		
		return execute;
	}

	
	
	
}
