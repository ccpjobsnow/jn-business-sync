package com.ccp.jn.sync.resumes.searchs.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.UseThisId;
import com.ccp.jn.sync.common.business.DownloadResumeToRecruiterAction;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;


public class DownloadResumeToRecruiter {
	
	private final DownloadResumeToRecruiterAction action = new DownloadResumeToRecruiterAction();
	
	public CcpMapDecorator execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		
		CcpMapDecorator result =  new UseThisId(values, new CcpMapDecorator())
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInEntity(JnEntity.denied_view_to_recruiter).returnStatus(JnProcessStatus.resumeHasBeenDeniedToRecruiter).and()
		.ifThisIdIsNotPresentInEntity(JnEntity.candidate_resume).returnStatus(JnProcessStatus.resumeNotFound).and()
		.ifThisIdIsPresentInEntity(JnEntity.candidate_resume).executeAction(this.action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
