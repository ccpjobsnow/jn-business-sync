package com.ccp.jn.sync.resumes.crud.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.jn.sync.common.business.DownloadThisResumeToHisOwner;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.jn.commons.JnEntity;


public class DownloadResumeToHisOwner {
	
	private final DownloadThisResumeToHisOwner action = CcpDependencyInjection.getInjected(DownloadThisResumeToHisOwner.class);
	
	@CcpDependencyInject
	private CcpDao dao;

	
	public CcpMapDecorator execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		
		CcpMapDecorator put = this.dao
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.candidate).executeAction(this.action).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.candidate).returnStatus(JnProcessStatus.candidateNotFound).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put;
	}
}
