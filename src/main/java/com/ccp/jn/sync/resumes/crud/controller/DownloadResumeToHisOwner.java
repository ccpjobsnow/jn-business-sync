package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpDaoCalculateId;
import com.ccp.jn.sync.business.JnSyncBusinessDownloadThisResumeToHisOwner;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.jn.commons.entities.JnEntity;


public class DownloadResumeToHisOwner {
	
	private final JnSyncBusinessDownloadThisResumeToHisOwner action = new JnSyncBusinessDownloadThisResumeToHisOwner();
	
	public CcpMapDecorator execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		
		CcpMapDecorator put =  new CcpDaoCalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.candidate).executeAction(this.action).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.candidate).returnStatus(JnProcessStatus.candidateNotFound).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put;
	}
}
