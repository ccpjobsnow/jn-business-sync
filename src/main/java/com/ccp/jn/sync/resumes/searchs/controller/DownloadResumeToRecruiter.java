package com.ccp.jn.sync.resumes.searchs.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpDaoCalculateId;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.ccp.jn.sync.business.JnSyncBusinessDownloadResumeToRecruiterAction;
import com.jn.commons.entities.JnEntityCandidateResume;
import com.jn.commons.entities.JnEntityDeniedViewToRecruiter;


public class DownloadResumeToRecruiter {
	
	private final JnSyncBusinessDownloadResumeToRecruiterAction action = new JnSyncBusinessDownloadResumeToRecruiterAction();
	
	public CcpMapDecorator execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		
		JnEntityCandidateResume entity = new JnEntityCandidateResume();
		CcpMapDecorator result =  new CcpDaoCalculateId(values)
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInEntity(new JnEntityDeniedViewToRecruiter()).returnStatus(JnProcessStatus.resumeHasBeenDeniedToRecruiter).and()
		.ifThisIdIsNotPresentInEntity(entity).returnStatus(JnProcessStatus.resumeNotFound).and()
		.ifThisIdIsPresentInEntity(entity).executeAction(this.action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;

	}
}
