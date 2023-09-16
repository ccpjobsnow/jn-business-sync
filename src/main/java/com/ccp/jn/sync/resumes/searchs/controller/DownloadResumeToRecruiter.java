package com.ccp.jn.sync.resumes.searchs.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpDaoCalculateId;
import com.ccp.jn.sync.business.JnSyncBusinessDownloadResumeToRecruiterAction;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.jn.commons.entities.JnEntity;


public class DownloadResumeToRecruiter {
	
	private final JnSyncBusinessDownloadResumeToRecruiterAction action = new JnSyncBusinessDownloadResumeToRecruiterAction();
	
	public CcpMapDecorator execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		
		CcpMapDecorator result =  new CcpDaoCalculateId(values)
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInEntity(JnEntity.denied_view_to_recruiter).returnStatus(JnProcessStatus.resumeHasBeenDeniedToRecruiter).and()
		.ifThisIdIsNotPresentInEntity(JnEntity.candidate_resume).returnStatus(JnProcessStatus.resumeNotFound).and()
		.ifThisIdIsPresentInEntity(JnEntity.candidate_resume).executeAction(this.action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
