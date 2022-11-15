package com.ccp.jn.sync.resumes.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.jn.sync.resumes.business.data.GetResumeData;
import com.jn.commons.JnBusinessEntity;

public class GetCandidate {

	@CcpEspecification
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
