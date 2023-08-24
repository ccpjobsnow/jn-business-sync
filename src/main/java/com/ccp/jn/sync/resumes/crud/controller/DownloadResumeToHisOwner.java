package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.UseThisId;
import com.ccp.jn.sync.common.business.DownloadThisResumeToHisOwner;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;


public class DownloadResumeToHisOwner {
	
	private final DownloadThisResumeToHisOwner action = new DownloadThisResumeToHisOwner();
	
	public CcpMapDecorator execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		
		CcpMapDecorator put =  new UseThisId(values, new CcpMapDecorator())
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.candidate).executeAction(this.action).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.candidate).returnStatus(JnProcessStatus.candidateNotFound).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put;
	}
}
