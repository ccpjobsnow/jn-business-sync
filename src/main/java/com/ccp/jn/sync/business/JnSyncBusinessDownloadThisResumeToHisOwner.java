package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityCandidate;
import com.jn.commons.entities.JnEntityCandidateViewResume;

public class JnSyncBusinessDownloadThisResumeToHisOwner implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {


	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		JnSyncBusinessDownloadResume downloadResume = new JnSyncBusinessDownloadResume();

		CcpMapDecorator candidate = values.getInternalMap("_entities").getInternalMap(new JnEntityCandidate().name());
		
		String viewType = values.getAsString("viewType");
		String resume = candidate.getAsString("resume");

		new JnEntityCandidateViewResume().createOrUpdate(new CcpMapDecorator().put("resume", resume).put("viewType", viewType));
		
		CcpMapDecorator execute = downloadResume.apply(values);
		
		return execute;
	}

	
	
	
}
