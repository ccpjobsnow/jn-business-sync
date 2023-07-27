package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.jn.sync.common.business.DownloadResumeToRecruiterAction;
import com.jn.commons.JnBusinessEntity;


public class DownloadResumeToRecruiter {
	
	private final DownloadResumeToRecruiterAction action = CcpDependencyInjection.getInjected(DownloadResumeToRecruiterAction.class);
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		
		CcpMapDecorator result = this.crud.useThisId(values)
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInTable(JnBusinessEntity.denied_view_to_recruiter).returnStatus(403).and()
		.ifThisIdIsNotPresentInTable(JnBusinessEntity.candidate_resume).returnStatus(404).and()
		.ifThisIdIsPresentInTable(JnBusinessEntity.candidate_resume).executeAction(this.action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result.content;

	}
}
