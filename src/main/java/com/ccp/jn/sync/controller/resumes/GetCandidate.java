package com.ccp.jn.sync.controller.resumes;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.jn.sync.business.resumes.data.GetResumeData;
import com.jn.commons.JnBusinessEntity;

public class GetCandidate {

	@CcpSpecification
	private CcpDbCrud crud;
	
	public Map<String, Object> execute (String resume){
		
		CcpMapDecorator resumeData = this.crud.findById(new CcpMapDecorator().put("resume", resume),  
			    new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.candidate_resume).put("status", 404)
			   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.candidate).put("status", 404)
			   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.candidate).put("action", new GetResumeData())
			);

		return resumeData.content;
	}	
}
