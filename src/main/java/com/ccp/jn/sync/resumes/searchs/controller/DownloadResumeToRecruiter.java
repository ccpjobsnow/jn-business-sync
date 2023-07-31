package com.ccp.jn.sync.resumes.searchs.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.jn.sync.common.business.DownloadResumeToRecruiterAction;
import com.jn.commons.JnEntity;


public class DownloadResumeToRecruiter {
	
	private final DownloadResumeToRecruiterAction action = CcpDependencyInjection.getInjected(DownloadResumeToRecruiterAction.class);
	
	@CcpDependencyInject
	private CcpDao dao;

	
	public CcpMapDecorator execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		
		CcpMapDecorator result = this.dao.useThisId(values)
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInEntity(JnEntity.denied_view_to_recruiter).returnStatus(403).and()
		.ifThisIdIsNotPresentInEntity(JnEntity.candidate_resume).returnStatus(404).and()
		.ifThisIdIsPresentInEntity(JnEntity.candidate_resume).executeAction(this.action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
