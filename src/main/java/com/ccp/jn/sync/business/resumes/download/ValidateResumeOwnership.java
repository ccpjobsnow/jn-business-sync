package com.ccp.jn.sync.business.resumes.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.commons.CcpFlow;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class ValidateResumeOwnership implements CcpProcess {

	private final String email;

	public ValidateResumeOwnership(String email) {
		this.email = email;
	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		CcpMapDecorator candidateResume = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.candidate_resume.name());
		
		boolean hasNotOwnership = candidateResume.getAsString("email").equals(this.email) == false;
		
		if(hasNotOwnership) {
			throw new CcpFlow(values, 403);
		}
		
		return values;
	}
	
	
	
}
