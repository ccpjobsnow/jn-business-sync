package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntity;

public class JnSyncBusinessDownloadThisResumeToHisOwner implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {


	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		JnSyncBusinessDownloadResume downloadResume = new JnSyncBusinessDownloadResume();

		CcpMapDecorator candidate = values.getInternalMap("_entities").getInternalMap(JnEntity.candidate.name());
		
		String viewType = values.getAsString("viewType");
		String resume = candidate.getAsString("resume");

		JnEntity.candidate_view_resume.createOrUpdate(new CcpMapDecorator().put("resume", resume).put("viewType", viewType));
		
		CcpMapDecorator execute = downloadResume.apply(values);
		
		return execute;
	}

	
	
	
}
