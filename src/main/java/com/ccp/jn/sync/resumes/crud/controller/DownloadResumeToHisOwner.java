package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.ccp.jn.sync.business.JnSyncBusinessDownloadThisResumeToHisOwner;
import com.jn.commons.entities.JnEntityCandidate;


public class DownloadResumeToHisOwner {
	
	private final JnSyncBusinessDownloadThisResumeToHisOwner action = new JnSyncBusinessDownloadThisResumeToHisOwner();
	
	public CcpMapDecorator execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		
		JnEntityCandidate entity = new JnEntityCandidate();
		CcpMapDecorator put =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(entity).executeAction(this.action).and()
			.ifThisIdIsNotPresentInEntity(entity).returnStatus(JnProcessStatus.candidateNotFound).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put;
	}
}
